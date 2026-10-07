# Stage 1: Build the React Frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend

# Copy frontend package.json and install dependencies
COPY frontend/package*.json ./
RUN npm install

# Copy frontend source and build
COPY frontend/ ./
RUN npm run build

# Stage 2: Build the Spring Boot Backend
FROM maven:3.9.6-eclipse-temurin-21 AS backend-build
WORKDIR /app/backend

# Copy backend pom.xml and download dependencies (for caching)
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B

# Copy backend source
COPY backend/src ./src

# Copy the frontend build output into Spring Boot's static resources directory
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static/

# Package the application (skip tests for faster deployment builds)
RUN mvn clean package -DskipTests

# Stage 3: Run the Application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built jar from the backend-build stage
COPY --from=backend-build /app/backend/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
