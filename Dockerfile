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

# Copia o artefato gerado no estágio de build
COPY --from=build /app/target/*.jar app.jar

# Declara a porta padrão
EXPOSE 8080

# Executa o JAR injetando a porta dinâmica do Render ($PORT) e escutando em 0.0.0.0
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Dserver.address=0.0.0.0 -jar app.jar"]