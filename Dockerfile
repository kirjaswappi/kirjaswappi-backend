# Stage 1: Build jar using Maven
FROM maven:3-sapmachine-24 AS build

WORKDIR /app

COPY . .

RUN mvn package -DskipTests

# Stage 2: Build native image using GraalVM
FROM ghcr.io/graalvm/native-image-community AS native-build

WORKDIR /app

# Copy the JAR from the Maven build stage
COPY --from=build /app/target/backend-1.0.0-SNAPSHOT.jar ./app.jar

# Build native executable from the jar
RUN native-image --no-fallback -jar app.jar --initialize-at-build-time=org.springframework.boot.loader.nio.file.NestedFileSystemProvider backend


# Stage 3: Minimal runtime image with native executable
FROM alpine:latest

WORKDIR /app

# Copy native executable from native-build stage
COPY --from=native-build /app/backend .

ENV SPRING_PROFILES_ACTIVE=cloud
ENV PORT=10000

EXPOSE $PORT

RUN chmod +x ./backend

CMD ["./backend"]