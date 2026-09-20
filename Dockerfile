# --- Etapa 1: build con Maven ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cachear dependencias antes de copiar el código fuente
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Etapa 2: imagen de ejecución ---
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
