# ==========================================
# Stage 1: Build aplikasi menggunakan Maven
# ==========================================
FROM maven:3.9-eclipse-temurin-25-alpine AS builder
WORKDIR /app

# Copy file pom.xml dan download dependencies (untuk caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy seluruh source code dan lakukan build (menghasilkan file JAR)
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Jalankan aplikasi (Production Image)
# ==========================================
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copy HANYA file JAR dari hasil build di Stage 1
COPY --from=builder /app/target/*.jar app.jar

# Dokumentasi port yang digunakan aplikasi
EXPOSE 8080

# Jalankan aplikasi Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]