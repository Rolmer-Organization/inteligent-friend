# inteligent-friend

POC de integração com a [API da Anthropic](https://docs.claude.com/) usando Spring Boot, feita com objetivo de aprendizado. O projeto é incrementado em fases, aumentando a complexidade a cada etapa (ver [Roadmap](#roadmap)).

## Stack

- Java 17 + Spring Boot 4.0.7 (`spring-boot-starter-webmvc`)
- Cliente oficial [`anthropic-java`](https://github.com/anthropics/anthropic-sdk-java) para a Messages API
- Bean Validation (`spring-boot-starter-validation`)
- Lombok

## Arquitetura

Fluxo atual da requisição:

```
Cliente HTTP
   │  POST /api/v1/chat  { message, maxTokens? }
   ▼
ChatController          → valida o corpo (Bean Validation) e delega ao service
   ▼
ChatService              → monta o MessageCreateParams e chama a Messages API
   ▼
AnthropicClient (SDK)    → autentica via ANTHROPIC_API_KEY e faz a chamada HTTP
   ▼
Anthropic API
```

Configuração por perfil (`application-{profile}.yaml`), com `local` como default — usa `claude-haiku-4-5` para iterar mais rápido/barato durante o desenvolvimento.

### Decisões técnicas

- **API key via variável de ambiente, não via `application.yaml`.** O `AnthropicClient` é criado com `AnthropicOkHttpClient.fromEnv()`, que lê `ANTHROPIC_API_KEY` do ambiente do sistema operacional. Evita que qualquer credencial precise existir em texto puro em arquivo versionável.
- **`maxTokens` opcional na request.** Quando omitido, cai no default configurado em `anthropic.max-tokens` (`application.yaml`), evitando obrigar o cliente da API a conhecer esse detalhe.

## Requisitos

- Java 17
- Maven (ou usar o wrapper `./mvnw` incluso)
- Uma API key válida da Anthropic

## Como rodar

1. Defina a variável de ambiente com sua API key:

   ```powershell
   $env:ANTHROPIC_API_KEY = "sua-chave-aqui"
   ```

2. Suba a aplicação (perfil `local` é o default):

   ```bash
   ./mvnw spring-boot:run
   ```

3. A API sobe em `http://localhost:8080`.

## Como rodar com Docker

1. Copie `.env.example` para `.env` e preencha sua API key:

   ```bash
   cp .env.example .env
   ```

2. Suba API + Redis com um comando:

   ```bash
   docker compose up --build
   ```

3. A API sobe em `http://localhost:8080`, perfil `prod` por padrão (ajustável via `SPRING_PROFILES_ACTIVE` no `.env`).

O `Dockerfile` usa build multi-stage (Maven só na etapa de build; a imagem final roda apenas o JRE, como usuário não-root). O `docker-compose.yml` já sobe um Redis junto, em preparação para a Fase 4 — hoje ele fica ocioso, sem ser usado pelo código ainda.

> Usa Podman em vez de Docker? `podman compose up --build` funciona da mesma forma.

## Como testar

```bash
curl -X POST http://localhost:8080/api/v1/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Explique em uma frase o que é injeção de dependência.", "maxTokens": 200}'
```

Resposta esperada:

```json
{ "response": "..." }
```

`maxTokens` é opcional; se omitido, usa o valor default (`anthropic.max-tokens`).

## Roadmap

Status atual: **Fase 3 concluída.**

1. ✅ Integração inicial — mensagem fixa enviada à API da Anthropic, código dividido em pacotes/classes.
2. ✅ Exposição via HTTP — mensagem passa a vir dinamicamente do corpo da requisição.
3. ✅ Clean code — `ChatService` dividido em `ChatMessageRequestFactory` (monta o request) e `ChatResponseExtractor` (extrai o texto da resposta), com testes unitários para cada um.
4. ⬜ Multi-mensagens — conversas com histórico, persistido em Redis.
5. ⬜ Inclui tool_uses.


## Pendências conhecidas

- Sem CI de build/testes ainda — previsto para uma fase futura.
- `spring-ai-starter-vector-store-redis` já está no `pom.xml` e o Redis já sobe via `docker-compose.yml`, mas ainda não é utilizado pelo código — preparação para a Fase 4.
