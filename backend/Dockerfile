# AccessAble backend — production Dockerfile for Render deployment
# Build stage: Java 21 JDK + Maven Wrapper
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven Wrapper files first (layer caching)
COPY mvnw mvnw.cmd ./
COPY .mvn/wrapper .mvn/wrapper/

# Make the wrapper script executable
RUN chmod +x mvnw

# Copy project descriptor and source
COPY pom.xml ./
COPY src ./src

# Build the executable JAR (tests are NOT skipped)
RUN ./mvnw -B clean package

# Runtime stage: Java 21 JRE
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create a non-root user for running the application
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy only the executable JAR from the build stage
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Change ownership to the non-root user
RUN chown -R appuser:appuser /app

USER appuser

# Render supplies PORT automatically; default to 8080 locally
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]