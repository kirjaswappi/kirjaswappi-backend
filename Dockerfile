# Stage 1: Build native executable
FROM ghcr.io/graalvm/native-image-community:latest AS build

# Only install Maven — GraalVM image already has native-image, gcc, etc.
RUN microdnf install -y maven && microdnf clean all

WORKDIR /app

COPY . .

RUN mvn -Pnative package -DskipTests

# Stage 2: Runtime image
FROM debian:bookworm-slim

WORKDIR /app
COPY --from=build /app/target/backend ./backend

RUN chmod +x ./backend

ENV SPRING_PROFILES_ACTIVE=cloud
ENV PORT=10000
EXPOSE 10000

CMD ["./backend"]