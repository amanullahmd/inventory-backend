# Backend - Spring Boot API

This directory will contain the Spring Boot backend API for the inventory management system.

## Planned Features

- RESTful API endpoints
- JWT authentication integration with Keycloak
- PostgreSQL database integration
- Business logic for inventory management
- User management endpoints
- Stock movement tracking
- Data validation and error handling

## Technology Stack

- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL
- **Authentication**: JWT with Keycloak integration
- **Build Tool**: Maven or Gradle
- **Testing**: JUnit 5, Mockito
- **Documentation**: OpenAPI/Swagger

## API Endpoints (Planned)

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Token refresh
- `POST /api/auth/logout` - User logout

### Items Management
- `GET /api/items` - List all items
- `POST /api/items` - Create new item
- `GET /api/items/{id}` - Get item details
- `PUT /api/items/{id}` - Update item
- `DELETE /api/items/{id}` - Delete item

### Stock Management
- `POST /api/stock/in` - Add stock
- `POST /api/stock/out` - Remove stock
- `GET /api/stock/movements` - Get stock movement history

### User Management (Admin only)
- `GET /api/users` - List users
- `POST /api/users` - Create user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

## Development Setup (Coming Soon)

```bash
cd backend
./mvnw spring-boot:run
```

## Status

🚧 **Under Development** - This component will be implemented next.# inventory-backend
