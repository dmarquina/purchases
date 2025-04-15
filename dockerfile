FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/purchases-0.0.1-SNAPSHOT.jar /app/purchases.jar

EXPOSE 8443
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "purchases.jar", "--spring.profiles.active=prod"]