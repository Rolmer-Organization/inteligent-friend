## Etapa de build: compila e empacota o jar com o Maven completo.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copia so o pom primeiro para cachear as dependencias em uma camada separada
# do codigo-fonte (mudar o codigo nao invalida esse cache).
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B package -DskipTests

## Etapa de runtime: imagem final so com o JRE e o jar, sem o Maven.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
