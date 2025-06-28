# Zero Trust Auth Gateway

A secure authentication gateway built with Java and Spring Boot, following Clean Architecture principles.  
This project is part of a portfolio focused on security-first design using modern best practices.

## ✅ Features

- User registration endpoint (`/api/v1/auth/register`)
- Clean Architecture (domain, application, infrastructure separation)
- Spring Security configured with endpoint access control
- In-memory H2 database for development
- H2 Web Console enabled for testing
- Profile-based environment configuration (dev/prod)

## 🛠️ Technologies Used

| Layer             | Technology                      |
|------------------|---------------------------------|
| Language          | Java 21                         |
| Framework         | Spring Boot                     |
| Architecture      | Clean Architecture              |
| Security          | Spring Security                 |
| Persistence       | JPA (Jakarta Persistence API)   |
| Database (Dev)    | H2 (in-memory)                  |
| Database Tooling  | Flyway (planned for production) |
| Build Tool        | Gradle with Kotlin DSL          |
| Environment Mgmt  | Spring Profiles (`dev`, `prod`) |
| API Testing       | Postman                         |

## 🚀 Getting Started

### Run in development mode (H2 memory DB)

```bash
./gradlew bootRun --args='--spring.profiles.active=ENV'

