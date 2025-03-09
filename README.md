[![Main](https://github.com/kirjaswappi/kirjaswappi-backend/actions/workflows/main.yml/badge.svg?branch=main)](https://github.com/kirjaswappi/kirjaswappi-backend/actions/workflows/main.yml)

# KirjaSwappi Backend Service

This repository contains the `kirjaswappi backend` service for the KirjaSwappi Platform. The KirjaSwappi backend service is a [Spring Boot](https://spring.io/projects/spring-boot) Application.

## Local Development Setup

To start developing, you first need to set up your local machine as described [here]().

To build the application and run all tests, execute

```console
mvn clean package
```

To run the application, you can run

```console
mvn spring-boot:run
```

This will run the application with the spring `local` profile with an embedded [H2 database](https://www.h2database.com/html/main.html) and unsecured HTTP Endpoints.

There is another profile `localH2File` to enhance `local` profile with a local H2 database file:

```console
mvn spring-boot:run -Dspring-boot.run.profiles=local,local-h2-file
```

The H2 local database file has the advantage to be used in IDE's (like IntelliJ Ultimate) directly.

To format the source code, you can run

```console
mvn spotless:apply
```

## API documentation

API documentation available on [swagger ui](https://api.kirjaswappi.fi/swagger-ui/index.html).

