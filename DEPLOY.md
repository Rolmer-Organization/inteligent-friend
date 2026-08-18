# Deploy automatizado para EC2

Runbook de setup único (rodar uma vez, com suas credenciais AWS). Depois disso,
todo merge em `master` builda a imagem, sobe pra ECR e atualiza a EC2 via
`.github/workflows/deploy.yml` — sem chave SSH nem access key da AWS guardada
no GitHub (tudo via OIDC + SSM).

Substitua `<ACCOUNT_ID>` e `<REGION>` pelos valores da sua conta em todos os
comandos abaixo (`aws sts get-caller-identity` mostra o account id).

## 1. Criar o repositório na ECR

```bash
aws ecr create-repository --repository-name inteligent-friend --region <REGION>
```

## 2. Criar o IAM OIDC provider do GitHub (se ainda não existir na conta)

```bash
aws iam create-open-id-connect-provider \
  --url https://token.actions.githubusercontent.com \
  --client-id-list sts.amazonaws.com \
  --thumbprint-list 6938fd4d98bab03faadb97b34396831e3780aea1
```

Se já existir (erro `EntityAlreadyExists`), pode ignorar.

## 3. Criar a IAM Role que o GitHub Actions vai assumir

Trust policy (`trust-policy.json`) — restringe a quem pode assumir a role a
pushes na branch `master` deste repo:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::<ACCOUNT_ID>:oidc-provider/token.actions.githubusercontent.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
        },
        "StringLike": {
          "token.actions.githubusercontent.com:sub": "repo:Rolmer-Organization/inteligent-friend:ref:refs/heads/master"
        }
      }
    }
  ]
}
```

Policy de permissões (`deploy-policy.json`):

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "EcrAuth",
      "Effect": "Allow",
      "Action": "ecr:GetAuthorizationToken",
      "Resource": "*"
    },
    {
      "Sid": "EcrPush",
      "Effect": "Allow",
      "Action": [
        "ecr:BatchCheckLayerAvailability",
        "ecr:PutImage",
        "ecr:InitiateLayerUpload",
        "ecr:UploadLayerPart",
        "ecr:CompleteLayerUpload",
        "ecr:BatchGetImage"
      ],
      "Resource": "arn:aws:ecr:<REGION>:<ACCOUNT_ID>:repository/inteligent-friend"
    },
    {
      "Sid": "DescribeInstance",
      "Effect": "Allow",
      "Action": "ec2:DescribeInstances",
      "Resource": "*"
    },
    {
      "Sid": "SsmDeploy",
      "Effect": "Allow",
      "Action": ["ssm:SendCommand", "ssm:GetCommandInvocation"],
      "Resource": "*"
    }
  ]
}
```

```bash
aws iam create-role \
  --role-name github-actions-inteligent-friend-deploy \
  --assume-role-policy-document file://trust-policy.json

aws iam put-role-policy \
  --role-name github-actions-inteligent-friend-deploy \
  --policy-name deploy-permissions \
  --policy-document file://deploy-policy.json
```

Guarde o ARN retornado (`arn:aws:iam::<ACCOUNT_ID>:role/github-actions-inteligent-friend-deploy`).

## 4. Dar permissão de ECR pull + SSM pra própria instância EC2

Se a instância ainda não tem uma IAM Role (instance profile) anexada, crie uma
com as policies gerenciadas `AmazonSSMManagedInstanceCore` (necessária para o
SSM Agent funcionar) e `AmazonEC2ContainerRegistryReadOnly`, depois anexe à
instância (via console: EC2 → instância → Actions → Security → Modify IAM role).

## 5. Configurar variáveis no GitHub

No repositório, em **Settings → Secrets and variables → Actions → Variables**
(são variáveis, não secrets — nenhum dos dois valores é sensível):

| Nome | Valor |
|---|---|
| `AWS_DEPLOY_ROLE_ARN` | ARN da role criada no passo 3 |
| `AWS_REGION` | `<REGION>` |

## 6. Preparar a instância EC2 (Docker + estrutura de deploy)

Conecte na instância (via SSM Session Manager, `aws ssm start-session --target <INSTANCE_ID>`,
ou SSH se ainda tiver acesso) e rode:

**Amazon Linux 2023:**
```bash
sudo dnf install -y docker
sudo systemctl enable --now docker
sudo curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 \
  -o /usr/local/lib/docker/cli-plugins/docker-compose
sudo chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
sudo usermod -aG docker ssm-user
```

**Ubuntu:**
```bash
sudo apt-get update && sudo apt-get install -y docker.io docker-compose-plugin
sudo systemctl enable --now docker
```

Depois, em qualquer uma das duas:

```bash
sudo mkdir -p /opt/inteligent-friend
sudo tee /opt/inteligent-friend/.env > /dev/null <<'EOF'
ANTHROPIC_API_KEY=sua-chave-real-aqui
EOF
```

Copie o `docker-compose.prod.yml` deste repositório para
`/opt/inteligent-friend/docker-compose.prod.yml` na instância (o workflow
não envia esse arquivo a cada deploy — só a imagem; se você editá-lo,
recopie manualmente).

## 7. Marcar a instância com a tag usada pelo workflow

O workflow localiza a instância pela tag `Name=inteligent-friend`
(`EC2_TAG_NAME` em `.github/workflows/deploy.yml`). Confirme essa tag em
EC2 → instância → Tags, ou ajuste o valor no workflow para o nome que você usa.

## Testando

Depois de tudo configurado, um merge em `master` já dispara o deploy. Para
forçar manualmente sem esperar um push:

```bash
gh workflow run "Deploy para EC2 (producao)" --ref master
```

## Limitações conhecidas desta primeira versão

- Sem HTTPS/reverse proxy — a API fica exposta em HTTP puro na porta 80.
  Considerar nginx + certbot ou um Application Load Balancer numa fase futura.
- `docker-compose.prod.yml` não é sincronizado automaticamente para a EC2;
  mudanças nele exigem copiar manualmente até o deploy ser expandido para isso.
