FROM maven:3.6.3-jdk-11-slim as build

WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn package -q -Dmaven.test.skip=true

FROM openjdk:11-jre-slim-buster

WORKDIR /app
EXPOSE 8080
ENV STORE_ENABLED=true
ENV WORKER_ENABLED=true
COPY --from=build /app/target/SpringBoot-1.0.jar /app

CMD ["java", "-jar", "SpringBoot-1.0.jar"]
