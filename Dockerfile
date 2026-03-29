# ─── Stage 1: Build the React Frontend ───
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# ─── Stage 2: Build the Spring Boot Backend ───
FROM maven:3.8.5-eclipse-temurin-17-alpine AS backend-build
WORKDIR /app/backend

# Copy the built frontend to the static target in backend
COPY --from=frontend-build /app/frontend/dist /app/backend/src/main/resources/static

# Build the JAR
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests

# ─── Stage 3: Final Runtime Image ───
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# The JAR file is built in Stage 2 inside /app/backend/target/
COPY --from=backend-build /app/backend/target/*.jar app.jar

# Standard port for Spring Boot
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
