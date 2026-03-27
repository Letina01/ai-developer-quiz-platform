FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /workspace

ARG SERVICE_NAME

COPY . .
RUN mvn -pl ${SERVICE_NAME} -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

ARG SERVICE_NAME
COPY --from=build /workspace/${SERVICE_NAME}/target/${SERVICE_NAME}-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
