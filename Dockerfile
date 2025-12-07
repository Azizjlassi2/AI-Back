# Étape 1 : Build de l'application (multi-stage build pour optimiser la taille)
FROM maven:3.8.6-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests  # Skip tests pour build rapide ; activez en CI


# Étape 2 : Runtime image légère
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod 
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]