# Zero Trust Auth Gateway

A secure authentication gateway built with Java and Spring Boot, following Clean Architecture principles.  
This project is part of a portfolio focused on security-first design using modern best practices.

---
## 📋 API Endpoints Overview

### Authentication (`/api/v1/auth`)

- `POST /token` - Login with rate limiting  
- `DELETE /token` - Logout  
- `POST /token/refresh` - JWT token refresh  
- `GET /status/{userId}` - User login status with rate limiting  

### User Management (public and profile) (`/api/v1/users`)

- `POST /` - Public user registration (rate limited)  
- `POST /activation` - Account activation via token (rate limited)  
- `POST /activation/resend` - Resend activation email (rate limited)  
- `PUT /me` - Update authenticated user profile  

### Admin User Management (`/api/v1/admin/users`)

- `POST /` - Create new user (admin)  
- `GET /` - List users with pagination (admin)  
- `GET /{userId}` - User details (admin)  
- `PUT /{userId}` - Update user (admin)  
- `PUT /{userId}/block` - Block user (admin)  
- `DELETE /{userId}/block` - Unblock user (admin)  
- `DELETE /{userId}` - Delete user (admin)  

### Password Reset (`/api/v1/users/password-reset`)

- `POST /request` - Request password reset email (rate limited)  
- `POST` - Reset password with token (rate limited)  

### MFA Management (`/api/v1/users/me/mfa`)

- `PUT` - Enable MFA for authenticated user  
- `DELETE` - Disable MFA for authenticated user  

### Login Policy (`/api/v1/policy`)

- `GET` - Get login policy (admin)  
- `PUT` - Update login policy (admin)  
---

# 📂 Postman Collection
A Postman collection has been created to simplify API testing, including all public endpoints with examples and environment variables.

---

## ✅ Features
- Clean Architecture (domain, application, infrastructure separation)
- Spring Security configured with endpoint access control
- In-memory H2 database for development
- H2 Web Console enabled for testing
- Profile-based environment configuration (`dev`, `prod`)
- Unit tests with high code coverage using JaCoCo

---

## 🛠️ Technologies Used

| Layer              | Technology                      |
|--------------------|--------------------------------|
| Language           | Java 21                         |
| Framework          | Spring Boot                     |
| Architecture       | Clean Architecture              |
| Security           | Spring Security + JWT           |
| Persistence        | JPA (Jakarta Persistence API)  |
| Database (Dev)     | H2 (in-memory)                  |
| Cache/Token Store  | Redis                          |
| Rate Limiting      | Bucket4j + Redis               |
| Message Broker     | (Planned) Kafka or RabbitMQ    |
| Database Tooling   | Flyway (planned for production)|
| Build Tool         | Gradle with Kotlin DSL          |
| Environment Mgmt   | Spring Profiles (`dev`, `prod`)|
| Testing            | JUnit 5 + Mockito               |
| Code Coverage Tool | JaCoCo                         |

---

## 🚀 Getting Started

### Run in development mode (H2 memory DB)

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

---

## 🧪 Run All Unit Tests with Coverage Report

```bash
./gradlew clean test jacocoTestReport
```

After running, open the report at:

```bash
build/reports/jacoco/test/html/index.html
```
---

# 🐳 Run with Docker (Postgres + Redis)
For local development, you can run Postgres and Redis using Docker.

Start containers with Docker Compose
## Start services in background
```bash
docker compose up -d
```

This will start:

* `Postgres on localhost:5432`
* `Redis on localhost:6379`

### Stop and remove containers
```
docker-compose down
```
To also remove volumes (clear data):
```
docker compose down -v
```

---

## 🔒 To Do

* Implement asynchronous email sending using Kafka or RabbitMQ to decouple email notifications

* Add comprehensive logging and monitoring

* Write integration tests with Cucumber

* Add Swagger/OpenAPI documentation for all endpoints

* Containerize application with Docker and define Docker Compose setup for local development

* Implement Flyway migrations fully for production environment

* Set up CI/CD pipelines for automated build, test, and deploy

---
