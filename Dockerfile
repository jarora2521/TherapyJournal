# syntax=docker/dockerfile:1.4

# 🏗️ Stage 1: Build the Spring Boot app with Maven and JDK 21
FROM --platform=$BUILDPLATFORM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /workdir/server

# Copy only pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the source code
COPY src ./src
RUN mvn package -DskipTests

# 📦 Stage 2: Extract the built JAR
FROM builder as prepare-production
RUN mkdir -p target/dependency
WORKDIR /workdir/server/target/dependency
RUN jar -xf ../*.jar

# 🚀 Stage 3: Run on lightweight Java 21 JRE
FROM eclipse-temurin:21-jre
EXPOSE 8080
VOLUME /tmp
ARG DEPENDENCY=/workdir/server/target/dependency
COPY --from=prepare-production ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=prepare-production ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=prepare-production ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.therapy.nest.TherapyNestApplication"]
