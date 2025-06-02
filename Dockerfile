# Builder stage: build native image
FROM maven:3-sapmachine-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -Pnative -DskipTests

# Runtime stage: minimal image
FROM alpine:latest
WORKDIR /app

# Copy native executable from builder
COPY --from=builder /app/target/backend .

# Set environment variables (optional)
ENV SPRING_PROFILES_ACTIVE=cloud
ENV PORT=10000

# Expose port
EXPOSE $PORT

# Make sure executable has permissions
RUN chmod +x ./backend

# Run the native executable
CMD ["./backend"]