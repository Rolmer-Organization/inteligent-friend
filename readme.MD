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

Status atual: **Fase 2 concluída.**

1. ✅ Integração inicial — mensagem fixa enviada à API da Anthropic, código dividido em pacotes/classes.
2. ✅ Exposição via HTTP — mensagem passa a vir dinamicamente do corpo da requisição.
3. ⬜ Clean code — divisão em classes e métodos mais especializados.
4. ⬜ Multi-mensagens — conversas com histórico, persistido em Redis.

## Pendências conhecidas

- Sem testes automatizados, Docker/Docker Compose e CI ainda — previstos para fases futuras.
- `spring-ai-starter-vector-store-redis` já está no `pom.xml` mas ainda não é utilizado — dependência adicionada em preparação para a Fase 4 (Redis).
