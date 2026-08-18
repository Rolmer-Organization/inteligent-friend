## Objetivo
Integrar com a API da Anthropic pra efeito de aprendizado. Isso é uma poc, e deve ser incrementada em complexidade e sofisticação, a medida que o aprendizaod for avançando

## Diretrizes gerais
1. Arquitetura e organização do código
   Separação em camadas — Controller → Service → Repository (ou Domain/Application/Infrastructure se for Clean Architecture/Hexagonal). Evite lógica de negócio no controller.
   Pacotes por feature ou por camada — com.empresa.projeto.pedido, com.empresa.projeto.cliente (feature-based) costuma escalar melhor e impressiona mais do que controller/service/repository genérico.
   DTOs separados de entidades — nunca exponha entidades JPA diretamente na API. Use DTOs de request/response e mapeadores (MapStruct é bem visto).
   Injeção de dependência via construtor — evite @Autowired em campo; construtor é testável e é o padrão recomendado hoje.
2. Stack e ferramentas "esperadas" no mercado
   Spring Boot (o mais comum) ou Quarkus/Micronaut se quiser se diferenciar
   Spring Data JPA + banco relacional (Postgres é mais valorizado que H2/MySQL só para portfólio)
   Flyway ou Liquibase para migrations — mostra maturidade, muita gente esquece
   Bean Validation (@Valid, @NotNull, etc.) nos DTOs
   Lombok (opcional, mas comum) — cuidado para não abusar
3. Qualidade e testes
   Testes unitários (JUnit 5 + Mockito) para a camada de serviço
   Testes de integração (Testcontainers é diferencial forte hoje — testar com banco real em container)
   Cobertura razoável, mas não precisa ser 100% — recrutadores técnicos preferem testes relevantes a números inflados
   Testes de contrato/API com RestAssured ou MockMvc
4. Documentação da API
   OpenAPI/Swagger (springdoc-openapi) — praticamente obrigatório hoje
   README completo: como rodar, requisitos, arquitetura, decisões técnicas (ADRs curtos são um diferencial e conectam com o que discutimos sobre engineering:architecture)
   Diagrama simples da arquitetura (pode ser um C4 ou até algo simples)
5. Segurança básica
   Nunca commitar credenciais — use variáveis de ambiente / application-dev.yml fora do git
   Se envolver autenticação: Spring Security + JWT é o padrão mais visto
   Tratamento de exceções centralizado (@ControllerAdvice + @ExceptionHandler) retornando erros padronizados (RFC 7807 - Problem Details é bem visto)
   Validação de entrada sempre (evitar SQL injection, mass assignment, etc.)
6. DevOps / entrega
   Dockerfile e docker-compose.yml (subir API + banco com um comando só) — isso é frequentemente o que mais impressiona porque mostra pensamento de produção
   CI simples com GitHub Actions (build + testes rodando a cada push) — sinaliza prática real de mercado
   Perfis de configuração (dev, test, prod)
7. Boas práticas de API REST
   Verbos HTTP corretos e status codes semânticos (201 para criação, 204 para delete, 404, 409, etc.)
   Paginação em listagens (Pageable do Spring Data)
   Versionamento de API (/api/v1/...)
   HATEOAS é opcional — bom conhecer mas raramente cobrado
8. Git e apresentação
   Commits atômicos e mensagens claras (conventional commits: feat:, fix:, refactor:) — muita gente avalia o histórico de commits, não só o código final
   Branches organizadas se simular fluxo de equipe (main, develop, feature/*)
   Nunca deixar código comentado ou "TODO" espalhado sem justificativa
   O que realmente diferencia um projeto de vaga

Na prática, o que costuma pesar mais na entrevista:

Testcontainers ao invés de H2 (mostra que você testa como em produção)
Docker Compose funcional (avaliador consegue rodar em 2 minutos)
Tratamento de erros consistente e padronizado
README que explica decisões, não só "como rodar"
Poucas features bem feitas > muitas features malfeitas


## Fases do estudo e do desenvolvimento
São prevista alGumas fases para o avanbço dessa poc

### Fase 1 - implementação inicial
instalar dependencias
fazer uma integração simples
Enviar uma mensagem e receber uma resposta
Divida o código em arquivos e pacotes tanto quando necessário para tornar a leitura fácil para alguém que não domina python
Padrões de projeto e clean code são importantes

### Fase 2 - implementação inicial
Fazer com que a API receba http requests
REmovar a mensagem hard coded e tornar dinâmica, vinda da request

### Fase 3 - Clean code
Dividir o código em classes e métodos especializados

### Fase 4 - implementar multi mensagens
Fazer com que o sistema permita que o usuário converse com a IA
implementar redis, para armazenar as mensagens do usuário e do assistente





