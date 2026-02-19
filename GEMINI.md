# Autolog Project

## Project Overview

This project is a vehicle management and tracking system built with Java and Spring Boot. It provides a RESTful API for managing users, vehicles, and fuel logs. The application uses JWT for authentication and a MySQL database for data persistence.

## Building and Running

To build and run the project, you can use the following Maven command:

```bash
./mvnw spring-boot:run
```

This will start the application on the default port (8080).

**Note:** You will need to have a MySQL database running and configured in the `src/main/resources/application.properties` file.

## Development Conventions

*   **RESTful API:** The application follows RESTful principles for its API design.
*   **JWT Authentication:** Authentication is handled using JSON Web Tokens (JWT).
*   **Spring Boot:** The application is built using the Spring Boot framework, which encourages convention over configuration.
*   **Lombok:** The project uses Lombok to reduce boilerplate code.
