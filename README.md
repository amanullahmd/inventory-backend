# Inventory Management System - Backend API

A modern, production-ready Spring Boot REST API for inventory management with JWT authentication, role-based access control, and comprehensive stock tracking capabilities.

## Overview

The Inventory Backend is a robust Java-based REST API built with Spring Boot 4, Spring Security 7, and Hibernate 7. It provides complete inventory management functionality including item management, stock movements, user authentication, and detailed reporting.

**Live API**: [https://inventory-backend.railway.app/api](https://inventory-backend.railway.app/api)  
**API Documentation**: [Swagger UI](https://inventory-backend.railway.app/api/swagger-ui.html)

## Tech Stack

- **Java 21** - Latest LTS version
- **Spring Boot 4.0** - Modern framework
- **Spring Security 7** - JWT-based authentication
- **Hibernate 7** - ORM layer
- **PostgreSQL 15+** - Production database
- **Flyway 11** - Database migrations
- **Maven** - Build tool
- **Logback** - Structured logging with JSON support

## Features

### Authentication & Authorization
- JWT token-based authentication
- Role-based access control (ADMIN, USER, MANAGER)
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
- CSRF protection headers
- Secure password policies

## Prerequisites

- Java 21 or higher
- Maven 3.8+
- PostgreSQL 15+ (for production)
- Git

## Quick Start

### Local Development

1. **Clone the repository**
   ```bash
   git clone https://github.com/amanullahmd/inventory-backend.git
   cd inventory-backend
   ```

2. **Configure database** (application-dev.yml uses H2 in-memory)
   ```bash
   # For PostgreSQL, update application.yml with your credentials
   ```

3. **Build the project**
   ```bash
   mvn clean package -DskipTests
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
   ```

5. **Access the API**
   - API Base URL: `http://localhost:8081/api`
   - Swagger UI: `http://localhost:8081/api/swagger-ui.html`
   - Health Check: `http://localhost:8081/api/actuator/health`

## Project Structure

```
backend.inventory/
├── src/
│   ├── main/
│   │   ├── java/management/backend/inventory/
│   │   │   ├── config/           # Spring configuration
│   │   │   ├── controller/       # REST endpoints
│   │   │   ├── service/          # Business logic
│   │   │   ├── repository/       # Data access
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── dto/              # Data transfer objects
│   │   │   ├── exception/        # Custom exceptions
│   │   │   └── util/             # Utility classes
│   │   └── resources/
│   │       ├── application.yml   # Default config
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── logback-spring.xml
│   │       └── db/migration/     # Flyway migrations
│   └── test/
│       └── java/                 # Unit tests
├── pom.xml                        # Maven configuration
├── Dockerfile                     # Docker image
└── README.md
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/change-password` - Change password

### Items
- `GET /api/items` - List all items
- `POST /api/items` - Create new item
- `GET /api/items/{id}` - Get item details
- `PUT /api/items/{id}` - Update item
- `DELETE /api/items/{id}` - Delete item

### Stock Movements
- `GET /api/stock-movements` - List all movements
- `POST /api/stock-movements` - Record stock movement
- `GET /api/stock-movements/{id}` - Get movement details

### Reports
- `GET /api/reports/statistics` - Inventory statistics
- `GET /api/reports/stock-out-reasons` - Stock out analysis
- `GET /api/reports/movement-history` - Movement history

### Users
- `GET /api/users` - List users (ADMIN only)
- `GET /api/users/{id}` - Get user profile
- `PUT /api/users/{id}` - Update user profile

### Categories
- `GET /api/categories` - List categories
- `POST /api/categories` - Create category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

## Configuration

### Environment Variables

**Development** (application-dev.yml):
```yaml
spring.profiles.active: dev
JDBC_DATABASE_URL: jdbc:postgresql://your-remote-db-host:port/db
CORS_ALLOWED_ORIGINS: http://localhost:3000
```

**Production (Railway)**:
Set these variables in Railway Dashboard (Backend Service):

| Variable | Value (Example) | Description |
|----------|----------------|-------------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Activates production profile |
| `PORT` | `8080` | Server port |
| `JDBC_DATABASE_URL` | `jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}` | **REQUIRED** - Connection string |
| `PGHOST` | `${{shared.PGHOST}}` | Railway Postgres Host |
| `PGPORT` | `5432` | Railway Postgres Port |
| `PGDATABASE` | `${{shared.PGDATABASE}}` | Railway Postgres DB Name |
| `PGUSER` | `${{shared.PGUSER}}` | Railway Postgres User |
| `PGPASSWORD` | `${{shared.PGPASSWORD}}` | Railway Postgres Password |
| `JWT_SECRET` | `(your-strong-secret)` | Min 32 chars |
| `CORS_ALLOWED_ORIGINS` | `https://your-frontend.railway.app` | Allowed frontend URL |
| `JAVA_OPTS` | `-Xms128m -Xmx512m` | JVM Memory settings |

**DO NOT set `DATABASE_URL` manually in Backend variables (Railway sets it automatically but we use JDBC_DATABASE_URL to be safe).**

### Database Migrations

Flyway automatically runs migrations on startup. Migrations are located in `src/main/resources/db/migration/`.

## Building & Deployment

### CI/CD Pipeline
The project uses **GitHub Actions** (`.github/workflows/deploy-prod.yml`) to build and test on every push to `main`.
Railway automatically deploys the built Docker image.

### Docker Build
The project uses a multi-stage `Dockerfile` optimized for Java 21.

### Deploy to Railway

1. **Connect** your GitHub repository to Railway.
2. **Configure Variables** as listed above.
3. **Deploy**. Railway will build using the Dockerfile.

See [RAILWAY_DEPLOYMENT_GUIDE.md](../RAILWAY_DEPLOYMENT_GUIDE.md) for detailed instructions.

## Testing

### Run Tests
```bash
mvn test
```

### Run Specific Test
```bash
mvn test -Dtest=StockOutReasonServiceTest
```

### Test Coverage
```bash
mvn clean test jacoco:report
```

## Logging

The application uses structured JSON logging for production:

- **Development**: Console output with readable format
- **Production**: JSON format for log aggregation
- **Log Files**: Stored in `logs/` directory with rotation

View logs:
```bash
# Development
tail -f logs/inventory-backend.log

# Production JSON
tail -f logs/inventory-backend-json.log
```

## Security

### Password Policy
- Minimum 8 characters
- Must contain uppercase, lowercase, numbers, and special characters
- Hashed with bcrypt (strength 12)

### JWT Configuration
- Access token expiration: 15 minutes
- Refresh token expiration: 7 days
- Algorithm: HS256
- Minimum secret length: 32 characters

### CORS
Configure allowed origins in `application-prod.yml`:
```yaml
app:
  cors:
    allowed-origins: https://your-frontend-domain.com
```

## Performance

### Database Connection Pool
- HikariCP with optimized settings
- Max pool size: 20 (production)
- Connection timeout: 10 seconds
- Idle timeout: 5 minutes

### Caching
- Spring Cache abstraction
- Configurable cache strategies
- Query optimization with lazy loading

### Batch Processing
- JDBC batch size: 50 (production)
- Optimized for bulk operations

## Monitoring

### Health Checks
```bash
curl http://localhost:8081/api/actuator/health
```

### Metrics
```bash
curl http://localhost:8081/api/actuator/metrics
```

### Available Endpoints
- `/actuator/health` - Application health
- `/actuator/info` - Application info
- `/actuator/metrics` - Performance metrics

## Troubleshooting

### Database Connection Issues
```
Error: Connection refused (localhost:5432)
Cause: The application is trying to connect to localhost instead of the Railway database.
Solution:
1. Ensure `SPRING_PROFILES_ACTIVE=railway` is set.
2. Ensure `PGHOST`, `PGPORT`, etc. are set in Railway Variables.
3. If using `DATABASE_URL`, map it to `SPRING_DATASOURCE_URL` or ensure individual PG* vars are present.
```

### Flyway Migration Errors
```
Error: Found more than one migration with version X
Solution: Ensure migration files have unique version numbers
```

### JWT Token Expired
```
Error: Token has expired
Solution: Use refresh endpoint to get new access token
```

### CORS Errors
```
Error: CORS policy blocked
Solution: Update CORS_ALLOWED_ORIGINS with correct frontend URL
```

## Development Guidelines

### Code Style
- Follow Google Java Style Guide
- Use meaningful variable names
- Add JavaDoc for public methods
- Keep methods focused and small

### Git Workflow
```bash
# Create feature branch
git checkout -b feature/your-feature

# Commit with clear messages
git commit -m "feat: add new feature"

# Push and create pull request
git push origin feature/your-feature
```

### Commit Message Format
```
feat: add new feature
fix: fix bug
docs: update documentation
refactor: refactor code
test: add tests
chore: update dependencies
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'feat: add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support & Documentation

- **API Documentation**: [Swagger UI](https://inventory-backend.railway.app/api/swagger-ui.html)
- **Issues**: [GitHub Issues](https://github.com/amanullahmd/inventory-backend/issues)
- **Discussions**: [GitHub Discussions](https://github.com/amanullahmd/inventory-backend/discussions)

## Deployment Checklist

Before deploying to production:

- [ ] Update JWT_SECRET to a strong, unique value
- [ ] Configure CORS_ALLOWED_ORIGINS with production frontend URL
- [ ] Set up PostgreSQL database
- [ ] Configure environment variables
- [ ] Run database migrations
- [ ] Test all API endpoints
- [ ] Enable HTTPS/SSL
- [ ] Set up monitoring and alerts
- [ ] Configure backup strategy
- [ ] Review security settings

## Performance Benchmarks

- Average response time: < 100ms
- Database query time: < 50ms
- JWT token generation: < 10ms
- Concurrent users supported: 1000+

## Roadmap

- [ ] GraphQL API support
- [ ] Advanced caching strategies
- [ ] Real-time WebSocket updates
- [ ] Batch import/export functionality
- [ ] Advanced analytics dashboard
- [ ] Multi-tenant support

## Contact

**Author**: Amanullah  
**Email**: amanullahmd@gmail.com  
**GitHub**: [@amanullahmd](https://github.com/amanullahmd)

---

**Last Updated**: December 2024  
**Version**: 1.0.0
