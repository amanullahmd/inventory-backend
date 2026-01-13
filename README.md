# Inventory Management System - Backend API

A modern, production-ready Spring Boot REST API for inventory management with JWT authentication, role-based access control, and comprehensive stock tracking capabilities.

## Overview

The Inventory Backend is a robust Java-based REST API built with Spring Boot 3.3, Spring Security 6, and Hibernate 6. It provides complete inventory management functionality including item management, stock movements, user authentication, and detailed reporting.

## Live Deployment URLs

### Local Development
| Endpoint | URL |
|----------|-----|
| API Base URL | `http://localhost:8080/api` |
| Swagger UI | `http://localhost:8080/api/swagger-ui.html` |
| API Docs (JSON) | `http://localhost:8080/api/v3/api-docs` |
| Health Check | `http://localhost:8080/api/health` |
| Actuator Health | `http://localhost:8080/api/actuator/health` |

### Railway Production (After Deployment)
| Endpoint | URL |
|----------|-----|
| API Base URL | `https://<your-app>.railway.app/api` |
| Swagger UI | `https://<your-app>.railway.app/api/swagger-ui.html` |
| API Docs (JSON) | `https://<your-app>.railway.app/api/v3/api-docs` |
| Health Check | `https://<your-app>.railway.app/api/health` |

## Tech Stack

- **Java 21** - Latest LTS version
- **Spring Boot 3.3.4** - Modern framework
- **Spring Security 6** - JWT-based authentication
- **Hibernate 6.5** - ORM layer
- **PostgreSQL 15+** - Production database
- **Flyway 10** - Database migrations (18 migrations)
- **Maven** - Build tool
- **Logback** - Structured logging
- **HikariCP** - Connection pooling
- **SpringDoc OpenAPI** - API documentation

## Features

### Authentication & Authorization
- JWT token-based authentication
- Role-based access control (ADMIN, USER, MANAGER, VIEWER)
- Secure password hashing with bcrypt
- Token refresh mechanism
- Password change enforcement

### Inventory Management
- Create, read, update, delete items
- Track item categories
- Manage stock levels
- Record stock movements (IN/OUT)
- Stock out reason tracking
- Real-time inventory updates
- Batch and warehouse management

### Reporting & Analytics
- Stock movement history
- Stock out reason breakdown
- Inventory statistics
- Audit logging for all operations
- Comprehensive reporting endpoints

### Security
- CORS configuration
- Rate limiting
- Input validation and sanitization
- SQL injection prevention
- Security headers configuration
- Secure password policies

## Prerequisites

- Java 21 or higher
- Maven 3.8+
- PostgreSQL 15+ (for production)
- Git

## Quick Start

### Local Development with Railway Profile

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd backend.inventory
   ```

2. **Run with Railway profile (connects to Railway PostgreSQL)**
   ```powershell
   # PowerShell (Windows)
   $env:DATABASE_URL='jdbc:postgresql://interchange.proxy.rlwy.net:57838/railway'
   $env:SPRING_DATASOURCE_USERNAME='postgres'
   $env:SPRING_DATASOURCE_PASSWORD='RIOQCXxEaiSuipevEQhBbfDJYokCeqQp'
   $env:SPRING_PROFILES_ACTIVE='railway'
   ./mvnw.cmd spring-boot:run
   ```

   ```bash
   # Bash (Linux/Mac)
   export DATABASE_URL='jdbc:postgresql://interchange.proxy.rlwy.net:57838/railway'
   export SPRING_DATASOURCE_USERNAME='postgres'
   export SPRING_DATASOURCE_PASSWORD='RIOQCXxEaiSuipevEQhBbfDJYokCeqQp'
   export SPRING_PROFILES_ACTIVE='railway'
   ./mvnw spring-boot:run
   ```

3. **Access the API**
   - Swagger UI: `http://localhost:8080/api/swagger-ui.html`
   - Health Check: `http://localhost:8080/api/health`
   - API Docs: `http://localhost:8080/api/v3/api-docs`

### Local Development with Dev Profile

```powershell
# Uses H2 in-memory database
$env:SPRING_PROFILES_ACTIVE='dev'
./mvnw.cmd spring-boot:run
```

Access at: `http://localhost:8081/api/swagger-ui.html`

## Railway Deployment

### Database Configuration

**Railway PostgreSQL Connection Details:**
```
Host: interchange.proxy.rlwy.net
Port: 57838
Database: railway
Username: postgres
Password: RIOQCXxEaiSuipevEQhBbfDJYokCeqQp
```

**Connection URLs:**
- PostgreSQL URL: `postgresql://postgres:RIOQCXxEaiSuipevEQhBbfDJYokCeqQp@interchange.proxy.rlwy.net:57838/railway`
- JDBC URL: `jdbc:postgresql://interchange.proxy.rlwy.net:57838/railway`

### Environment Variables for Railway

Set these in Railway Dashboard → Variables:

| Variable | Value | Description |
|----------|-------|-------------|
| `SPRING_PROFILES_ACTIVE` | `railway` | Activates railway profile |
| `DATABASE_URL` | `jdbc:postgresql://interchange.proxy.rlwy.net:57838/railway` | JDBC connection string |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `RIOQCXxEaiSuipevEQhBbfDJYokCeqQp` | Database password |
| `JWT_SECRET` | `your-secret-key-min-32-characters` | JWT signing key |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,https://your-frontend.railway.app` | Allowed origins |

### Database Migrations

The application uses Flyway for database migrations. All 18 migrations run automatically on startup:

| Version | Description |
|---------|-------------|
| V1 | Grades and users tables |
| V2 | Categories |
| V3 | Suppliers |
| V4 | Warehouses |
| V5 | Items |
| V6 | Stock out reasons |
| V7 | Stock movements |
| V8 | Batches and pricing |
| V9 | Inventory transactions |
| V10 | Purchase orders |
| V11 | Sales orders |
| V12 | Stock transfers |
| V13 | Employees |
| V14 | Demands |
| V15 | Stock outs |
| V16 | Audit logs |
| V17 | Materialized views |
| V18 | Audit triggers |

### Reset Database (Drop All Tables)

To reset the Railway database and re-run migrations:

```powershell
# Connect to Railway PostgreSQL and drop all tables
$env:PGPASSWORD='RIOQCXxEaiSuipevEQhBbfDJYokCeqQp'
psql -h interchange.proxy.rlwy.net -p 57838 -U postgres -d railway -c "DROP SCHEMA public CASCADE; CREATE SCHEMA public; GRANT ALL ON SCHEMA public TO postgres; GRANT ALL ON SCHEMA public TO public;"

# Then restart the application to re-run migrations
```

## Project Structure

```
backend.inventory/
├── src/
│   ├── main/
│   │   ├── java/management/backend/inventory/
│   │   │   ├── config/           # Spring configuration
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── DataSourceConfig.java
│   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── controller/       # REST endpoints
│   │   │   │   ├── NativeAuthController.java
│   │   │   │   ├── ItemController.java
│   │   │   │   ├── StockController.java
│   │   │   │   ├── CategoryController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── ReportController.java
│   │   │   │   └── HealthController.java
│   │   │   ├── service/          # Business logic
│   │   │   ├── repository/       # Data access (22 repositories)
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── dto/              # Data transfer objects
│   │   │   ├── exception/        # Custom exceptions
│   │   │   └── util/             # Utility classes
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-railway.yml
│   │       ├── application-prod.yml
│   │       ├── logback-spring.xml
│   │       └── db/migration/     # 18 Flyway migrations
│   └── test/
│       └── java/                 # Unit & integration tests
├── pom.xml
├── Dockerfile
└── README.md
```

## API Endpoints

### Authentication (`/api/auth`)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user | No |
| POST | `/auth/login` | Login and get JWT | No |
| POST | `/auth/refresh` | Refresh access token | Yes |
| POST | `/auth/change-password` | Change password | Yes |
| GET | `/auth/me` | Get current user profile | Yes |

### Items (`/api/items`)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/items` | List all items | Yes |
| POST | `/items` | Create new item | Yes (ADMIN/MANAGER) |
| GET | `/items/{id}` | Get item details | Yes |
| PUT | `/items/{id}` | Update item | Yes (ADMIN/MANAGER) |
| DELETE | `/items/{id}` | Delete item | Yes (ADMIN) |
| GET | `/items/search` | Search items | Yes |

### Stock Movements (`/api/stock`)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/stock/movements` | List all movements | Yes |
| POST | `/stock/in` | Record stock in | Yes |
| POST | `/stock/out` | Record stock out | Yes |
| GET | `/stock/movements/{id}` | Get movement details | Yes |
| GET | `/stock/history` | Get movement history | Yes |

### Categories (`/api/categories`)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/categories` | List categories | Yes |
| POST | `/categories` | Create category | Yes (ADMIN/MANAGER) |
| PUT | `/categories/{id}` | Update category | Yes (ADMIN/MANAGER) |
| DELETE | `/categories/{id}` | Delete category | Yes (ADMIN) |

### Users (`/api/users`)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/users` | List users | Yes (ADMIN) |
| GET | `/users/{id}` | Get user profile | Yes |
| PUT | `/users/{id}` | Update user | Yes |
| DELETE | `/users/{id}` | Delete user | Yes (ADMIN) |

### Reports (`/api/reports`)
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/reports/statistics` | Inventory statistics | Yes |
| GET | `/reports/stock-out-reasons` | Stock out analysis | Yes |
| GET | `/reports/movement-history` | Movement history | Yes |

### Health & Monitoring
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/health` | Application health | No |
| GET | `/actuator/health` | Detailed health | No |
| GET | `/actuator/info` | Application info | No |
| GET | `/actuator/metrics` | Performance metrics | No |

## API Usage Examples

### 1. Register a New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "SecurePass123!",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### 2. Login and Get JWT Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "SecurePass123!"
  }'
```

Response:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 315360000000
}
```

### 3. Create an Item (Authenticated)
```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "name": "Widget A",
    "sku": "WGT-001",
    "description": "A sample widget",
    "categoryId": 1,
    "unitPrice": 29.99,
    "reorderLevel": 10
  }'
```

### 4. Record Stock In
```bash
curl -X POST http://localhost:8080/api/stock/in \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "itemId": 1,
    "quantity": 100,
    "notes": "Initial stock"
  }'
```

### 5. Record Stock Out
```bash
curl -X POST http://localhost:8080/api/stock/out \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "itemId": 1,
    "quantity": 10,
    "reason": "SALE",
    "notes": "Sold to customer"
  }'
```

### 6. Get Inventory Statistics
```bash
curl -X GET http://localhost:8080/api/reports/statistics \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 7. Health Check
```bash
curl http://localhost:8080/api/health
```

Response:
```json
{
  "status": "UP",
  "timestamp": 1768334402243,
  "message": "Application and database are healthy",
  "details": {
    "database": "PostgreSQL",
    "status": "UP",
    "timestamp": 1768334402243,
    "uptime": 144933744
  }
}
```

## Swagger UI Usage

1. Open Swagger UI: `http://localhost:8080/api/swagger-ui.html`
2. Click "Authorize" button (top right)
3. Enter your JWT token: `Bearer <your-token>`
4. Click "Authorize" and close the dialog
5. Now you can test all authenticated endpoints

## Configuration Profiles

| Profile | Port | Database | Use Case |
|---------|------|----------|----------|
| `dev` | 8081 | H2 In-Memory | Local development |
| `railway` | 8080 | Railway PostgreSQL | Railway deployment |
| `prod` | 8080 | PostgreSQL | Production |
| `staging` | 8080 | PostgreSQL | Staging environment |

## Testing

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=ItemServiceTest
```

### Run with Coverage Report
```bash
./mvnw clean test jacoco:report
```

Coverage report: `target/site/jacoco/index.html`

## Troubleshooting

### Database Connection Issues
```
Error: Connection refused
Solution: Verify DATABASE_URL, username, and password are correct
```

### Flyway Migration Errors
```
Error: Migration checksum mismatch
Solution: Run Flyway repair or reset database
```

### JWT Token Expired
```
Error: Token has expired
Solution: Use /auth/refresh endpoint to get new token
```

### CORS Errors
```
Error: CORS policy blocked
Solution: Add frontend URL to CORS_ALLOWED_ORIGINS
```

### PL/pgSQL Syntax Error (V18 Migration)
```
Error: syntax error at or near "$"
Solution: Ensure function delimiters use $$ not single $
```

## Security

### Password Policy
- Minimum 8 characters
- Must contain uppercase, lowercase, numbers, and special characters
- Hashed with bcrypt (strength 12)

### JWT Configuration
- Access token expiration: 10 years (configurable)
- Refresh token expiration: 10 years (configurable)
- Algorithm: HS256
- Minimum secret length: 32 characters

## Monitoring

### Health Check Response
```json
{
  "status": "UP",
  "message": "Application and database are healthy",
  "details": {
    "database": "PostgreSQL",
    "status": "UP"
  }
}
```

### Available Actuator Endpoints
- `/actuator/health` - Application health
- `/actuator/info` - Application info
- `/actuator/metrics` - Performance metrics

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Contact

**Author**: Amanullah  
**Email**: amanullahmd@gmail.com

---

**Last Updated**: January 2026  
**Version**: 1.0.0
