# Inventory Management Service

A production-grade RESTful API service for managing inventory with Categories, Products, and SKUs, built with Spring Boot and PostgreSQL.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Database Setup](#database-setup)
- [Running the Application](#running-the-application)
- [Running Tests](#running-tests)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Logging](#logging)
- [Code Coverage](#code-coverage)
- [Development Guidelines](#development-guidelines)

## Overview

The Inventory Management Service provides a complete API solution for managing product inventory across three main entities:

- **Categories**: Organize products into logical groups
- **Products**: Individual items with pricing and details
- **SKUs (Stock Keeping Units)**: Specific variants of products with unique identifiers and quantities

## Features

✅ **Complete CRUD Operations** for Categories, Products, and SKUs  
✅ **Advanced Search & Filtering** for products (by name, category, price range)  
✅ **Pagination & Sorting** for efficient data retrieval  
✅ **Strong Data Validation** with Bean Validation  
✅ **Relational Integrity** with JPA relationships  
✅ **Global Exception Handling** with consistent error responses  
✅ **Comprehensive Logging** with Logback  
✅ **API Documentation** with Swagger/OpenAPI 3  
✅ **High Test Coverage** with JUnit 5 and Mockito  
✅ **Production-Ready Configuration** with profiles  

## Tech Stack

### Core Framework
- **Java**: 8
- **Spring Boot**: 2.7.18
- **Spring Web**: REST API support
- **Spring Data JPA**: Database abstraction
- **Hibernate**: ORM framework

### Database
- **PostgreSQL**: 12+ (production)
- **H2**: In-memory database (testing)

### Build & Dependencies
- **Maven**: 3.6+
- **Lombok**: Reduce boilerplate code

### Testing
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework
- **Spring Boot Test**: Integration testing
- **JaCoCo**: Code coverage reporting

### Documentation & Validation
- **Springdoc OpenAPI**: API documentation
- **Hibernate Validator**: Bean validation

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 8** or higher
  ```bash
  java -version
  ```

- **Apache Maven 3.6+**
  ```bash
  mvn -version
  ```

- **PostgreSQL 12+**
  ```bash
  psql --version
  ```

- **Git** (for version control)
  ```bash
  git --version
  ```

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd inventory-management-service
```

### 2. Configure Application Properties

Edit `src/main/resources/application.yml` to configure your database connection:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/inventory_db
    username: postgres
    password: your_password
```

### 3. Build the Project

```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile the code
- Run all tests
- Generate JaCoCo coverage report
- Create the executable JAR file

## Database Setup

### Option 1: Using PostgreSQL (Recommended for Production)

1. **Create the database:**

```sql
CREATE DATABASE inventory_db;
```

2. **Create a user (optional):**

```sql
CREATE USER inventory_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE inventory_db TO inventory_user;
```

3. **Update application.yml** with your credentials

### Option 2: Let Spring Boot Create Tables Automatically

The application is configured with `spring.jpa.hibernate.ddl-auto=update`, which will automatically create/update tables based on entity definitions.

> **Note**: For production, consider using database migration tools like Flyway or Liquibase.

## Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using the JAR file

```bash
java -jar target/inventory-management-service-1.0.0-SNAPSHOT.jar
```

### With a specific profile

```bash
# Development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Production profile
java -jar target/inventory-management-service-1.0.0-SNAPSHOT.jar --spring.profiles.active=prod
```

The application will start on **http://localhost:8080**

### Verify the application is running

```bash
curl http://localhost:8080/api/v1/actuator/health
```

## Running Tests

### Run all tests

```bash
mvn test
```

### Run tests with coverage report

```bash
mvn clean verify
```

Coverage report will be generated at: `target/site/jacoco/index.html`

### Run specific test class

```bash
mvn test -Dtest=CategoryServiceTest
```

### Run tests in a specific package

```bash
mvn test -Dtest=com.inventory.management.service.*
```

## API Documentation

### Swagger UI

Once the application is running, access the interactive API documentation:

**URL**: http://localhost:8080/api/v1/swagger-ui.html

### OpenAPI JSON

**URL**: http://localhost:8080/api/v1/api-docs

## Project Structure

```
inventory-management-service/
├── src/main/java/com/inventory/management/
│   ├── controller/         # REST API endpoints
│   ├── service/           # Business logic
│   ├── repository/        # Data access layer
│   ├── entity/           # JPA entities
│   ├── dto/              # Request/Response DTOs
│   ├── mapper/           # Entity-DTO converters
│   ├── exception/        # Exception handling
│   ├── config/           # Configuration classes
│   └── util/             # Utility classes
├── src/main/resources/
│   ├── application.yml              # Main configuration
│   └── logback-spring.xml          # Logging configuration
├── src/test/              # Unit and integration tests
└── pom.xml               # Maven configuration
```

For detailed structure, see [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)

## API Endpoints

### Base URL: `http://localhost:8080/api/v1`

### Categories

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/categories` | Create a new category |
| GET | `/categories/{id}` | Get category by ID |
| GET | `/categories` | List all categories |
| PUT | `/categories/{id}` | Update category |
| DELETE | `/categories/{id}` | Delete category |

### Products

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/products` | Create a new product |
| GET | `/products/{id}` | Get product by ID |
| GET | `/products` | List/search products with filters |
| PUT | `/products/{id}` | Update product |
| DELETE | `/products/{id}` | Delete product |

**Query Parameters for GET /products:**
- `search`: Search by product name
- `category`: Filter by category ID
- `minPrice`: Minimum price filter
- `maxPrice`: Maximum price filter
- `page`: Page number (default: 0)
- `size`: Page size (default: 20)
- `sort`: Sort field and direction (e.g., `name,asc`)

### SKUs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/skus` | Create a new SKU |
| GET | `/skus/{id}` | Get SKU by ID |
| GET | `/skus` | List SKUs (optionally by product) |
| PUT | `/skus/{id}` | Update SKU |
| DELETE | `/skus/{id}` | Delete SKU |

## Configuration

### Application Profiles

The application supports multiple profiles:

- **default**: Uses PostgreSQL (development)
- **test**: Uses H2 in-memory database
- **dev**: Development environment (optional)
- **prod**: Production environment (optional)

### Environment Variables

You can override configuration using environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/inventory_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your_password
export SERVER_PORT=8080
```

### Key Configuration Options

Edit `application.yml` to customize:

- Database connection settings
- Server port
- Logging levels
- JPA/Hibernate behavior
- Swagger UI settings

## Logging

Logs are written to:
- **Console**: Colored output with timestamps
- **File**: `logs/inventory-management.log`

### Log Levels

```yaml
logging:
  level:
    root: INFO
    com.inventory.management: DEBUG
    org.hibernate.SQL: DEBUG
```

### View logs

```bash
tail -f logs/inventory-management.log
```

## Code Coverage

### Generate Coverage Report

```bash
mvn clean verify
```

### View Coverage Report

Open `target/site/jacoco/index.html` in your browser.

### Coverage Requirements

The project enforces a minimum of **80% line coverage** for all packages.

## Development Guidelines

### Code Style

- Follow Java naming conventions
- Use Lombok to reduce boilerplate (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- Keep methods focused and single-purpose
- Write self-documenting code with clear variable names

### Testing Best Practices

1. **Unit Tests**: Test business logic in services with mocked dependencies
2. **Integration Tests**: Test controllers and repositories with real Spring context
3. **Test Naming**: Use descriptive names (e.g., `shouldThrowExceptionWhenCategoryNotFound`)
4. **Coverage**: Aim for 80%+ coverage
5. **Assertions**: Use AssertJ or JUnit assertions

### API Design Principles

- Use proper HTTP methods (GET, POST, PUT, DELETE)
- Return appropriate status codes (200, 201, 400, 404, 500)
- Validate all inputs with `@Valid`
- Use DTOs for request/response
- Document endpoints with Swagger annotations

### Error Handling

All errors follow a consistent format:

```json
{
  "timestamp": "2026-01-04T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: '123'",
  "path": "/api/v1/products/123",
  "validationErrors": []
}
```

## Example API Calls

### Create a Category

```bash
curl -X POST http://localhost:8080/api/v1/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices and accessories"
  }'
```

### Create a Product

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 1299.99,
    "categoryId": 1
  }'
```

### Search Products

```bash
curl "http://localhost:8080/api/v1/products?search=laptop&minPrice=1000&maxPrice=2000&page=0&size=10&sort=price,asc"
```

### Create a SKU

```bash
curl -X POST http://localhost:8080/api/v1/skus \
  -H "Content-Type: application/json" \
  -d '{
    "skuCode": "LAP-001-BLK",
    "quantity": 50,
    "productId": 1,
    "attributes": {
      "color": "Black",
      "size": "15 inch"
    }
  }'
```

## Troubleshooting

### Application won't start

1. Check if PostgreSQL is running: `pg_isready`
2. Verify database credentials in `application.yml`
3. Check port 8080 is not in use: `netstat -an | findstr 8080` (Windows)

### Tests failing

1. Ensure H2 database dependency is in `pom.xml`
2. Check `application-test.yml` configuration
3. Run with verbose output: `mvn test -X`

### Database connection issues

1. Verify PostgreSQL is accepting connections
2. Check firewall settings
3. Verify user permissions: `psql -U postgres -d inventory_db`

## Contributing

1. Create a feature branch: `git checkout -b feature/my-feature`
2. Make your changes and add tests
3. Ensure tests pass: `mvn test`
4. Check code coverage: `mvn verify`
5. Commit your changes: `git commit -m "Add my feature"`
6. Push to the branch: `git push origin feature/my-feature`

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Contact

For questions or support, please contact the Inventory Team at inventory@example.com

---

**Built with ❤️ using Spring Boot**
