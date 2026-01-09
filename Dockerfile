# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copia apenas o necessário para cache de dependências
COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle

RUN chmod +x ./gradlew

# Baixa dependências (sem falhar por dependency verification no container)
RUN ./gradlew --no-daemon -Dorg.gradle.dependency.verification=off dependencies

# Agora copia o código da aplicação
COPY . .

# Garantir permissão após o COPY
RUN chmod +x ./gradlew

# Build do jar (bootJar padrão do Spring Boot)
RUN ./gradlew bootJar --no-daemon -Dorg.gradle.dependency.verification=off

# Imagem final
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Ajuste o nome do jar se for diferente
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
