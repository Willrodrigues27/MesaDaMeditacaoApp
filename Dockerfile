# Estágio 1: Build da aplicação usando Maven nativo
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia todo o código-fonte para o container
COPY . .

# Compila o projeto gerando o .jar
RUN mvn clean package -DskipTests

# Estágio 2: Execução da aplicação
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copia o artefato gerado
COPY --from=build /app/target/*.jar app.jar

# Informa ao Docker que a porta pode variar
EXPOSE 8081

# Executa o JAR forçando o Spring a pegar a variável PORT e aceitar conexões externas
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Dserver.address=0.0.0.0 -jar app.jar"]