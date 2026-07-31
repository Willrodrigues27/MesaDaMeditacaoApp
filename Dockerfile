# Estágio 1: Build da aplicação usando Maven Wrapper
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copia os arquivos do projeto
COPY . .

# Concede permissão de execução ao mvnw e realiza o package
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Estágio 2: Execução da aplicação
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]