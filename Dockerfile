# ── Stage 1: Build React Frontend (Vite) ──
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

# ── Stage 2: Build Spring Boot Backend with Embedded Frontend ──
FROM maven:3.9.6-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app

# Copy Maven POM and source
COPY backend/pom.xml ./backend/
COPY backend/src ./backend/src

# Copy compiled React frontend into Spring Boot static web directory
COPY --from=frontend-builder /app/frontend/dist ./backend/src/main/resources/static

# Copy ML Engine artifacts for Demand Prediction
COPY ml_engine ./ml_engine

WORKDIR /app/backend
RUN mvn clean package -DskipTests

# ── Stage 3: Lightweight Production JRE Image ──
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built standalone JAR
COPY --from=backend-builder /app/backend/target/smartshelf-backend-1.0.0.jar app.jar

# Copy ML metadata artifacts
COPY ml_engine ./ml_engine

# Render sets PORT dynamically (default 8093)
ENV PORT=8093
EXPOSE 8093

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
