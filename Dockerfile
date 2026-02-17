# Build stage (optional)
FROM maven:3.9.4-eclipse-temurin-17 AS spring
WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=spring /app/target/medical-appointment-api-0.0.1-SNAPSHOT.jar /app/medical-appointment-api.jar
CMD ["java", "-jar", "/app/medical-appointment-api.jar"]
