# Zero Trust Auth Gateway

A secure authentication gateway built with Java and Spring Boot, following Clean Architecture principles.  
This project is part of a portfolio focused on security-first design using modern best practices.

---

## ✅ Features

- User registration endpoint (`/api/v1/auth/register`)
- Login endpoint with JWT generation (`/api/v1/auth/login`)
- Clean Architecture (domain, application, infrastructure separation)
- Spring Security configured with endpoint access control
- In-memory H2 database for development
- H2 Web Console enabled for testing
- Profile-based environment configuration (`dev`, `prod`)
- Unit tests with high code coverage using JaCoCo

---

## 🛠️ Technologies Used

| Layer              | Technology                      |
|-------------------|---------------------------------|
| Language           | Java 21                         |
| Framework          | Spring Boot                     |
| Architecture       | Clean Architecture              |
| Security           | Spring Security + JWT           |
| Persistence        | JPA (Jakarta Persistence API)   |
| Database (Dev)     | H2 (in-memory)                  |
| Database Tooling   | Flyway (planned for production) |
| Build Tool         | Gradle with Kotlin DSL          |
| Environment Mgmt   | Spring Profiles (`dev`, `prod`) |
| Testing            | JUnit 5 + Mockito               |
| Code Coverage Tool | JaCoCo                          |

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

```
build/reports/jacoco/test/html/index.html
```

---

## 🔒 To Do

- Add validation and error handling to API layer
- Implement role-based authorization
- Persist roles in user registration
- Integrate Flyway for schema versioning
- Improve exception handling and logging
- Set up CI/CD pipelines for automated builds and tests
- **Create Dockerfile for containerizing the application**
- **Add Kubernetes manifests for orchestration and deployment in a cluster**

---
