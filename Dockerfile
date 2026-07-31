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
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]