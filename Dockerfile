# ======================  Estágio de build  ======================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia somente o pom para aproveitar cache das dependências
COPY pom.xml .
RUN mvn -B dependency:go-offline              # baixa dependências

# Copia o restante do código-fonte
COPY src ./src
RUN mvn -B package -DskipTests                # compila o JAR

# =====================  Estágio de runtime  =====================
FROM openjdk:17-slim

# 1) Instala bibliotecas de fontes exigidas pelo libfontmanager
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        libfreetype6 \
        libfontconfig1 \
        fonts-dejavu-core && \
    rm -rf /var/lib/apt/lists/*

# 2) Cria usuário não-root para executar a aplicação
RUN useradd -r -s /bin/false faturium

WORKDIR /app

# 3) Copia o JAR gerado no estágio de build
COPY --from=build /app/target/*.jar app.jar
RUN chown faturium:faturium app.jar

USER faturium

EXPOSE 8080

# 4) Flags da JVM + modo headless
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    JAVA_TOOL_OPTIONS="-Djava.awt.headless=true"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]