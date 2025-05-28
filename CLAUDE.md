s# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build and Run
- `./mvnw clean install` - Build the project
- `./mvnw spring-boot:run` - Run the application locally
- `./mvnw test` - Run all tests
- `./mvnw test -Dtest=ClassName` - Run a specific test class
- `./mvnw test -Dtest=ClassName#methodName` - Run a specific test method

### Development Tools
- `./mvnw compile` - Compile source code only
- `./mvnw clean` - Clean build artifacts

## Architecture

This is a Spring Boot application (version 4.0.0-SNAPSHOT) with the following key characteristics:

- **Java 17** - Target Java version
- **Package Structure**: `de.hirthe.gefrierschrankapp` - German naming suggests this is a freezer/refrigerator management app
- **Spring Framework Stack**:
  - Spring Boot Web (REST APIs)
  - Spring Data JPA (Database access)
  - Spring Security (Authentication/Authorization)
  - Spring Boot Actuator (Monitoring endpoints)
- **Development Tools**:
  - Spring Boot DevTools (Hot reload during development)
  - Lombok (Code generation for getters/setters/etc.)
- **Testing**: JUnit 5 with Spring Boot Test and Spring Security Test

The application follows standard Spring Boot project structure with main application class at `GefrierschrankAppApplication.java`. Currently minimal with basic Spring Boot starter configuration.

Note: The project uses Spring Boot 4.0.0-SNAPSHOT which is a development version requiring snapshot repositories.