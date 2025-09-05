FROM maven:3.9-eclipse-temurin-24 AS build

WORKDIR /app
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline


COPY src ./src
RUN mvn -B -DskipTests clean package

# Runtime stage
FROM eclipse-temurin:24-jre
ENV APP_HOME=/app
WORKDIR ${APP_HOME}

RUN groupadd --system app \
 && useradd --system --gid app --create-home --home-dir /home/app app

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

USER app
ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]