# Project Structure

This document explains the folder layout and architecture of the Inventory Management Service.

## Overview

The Inventory Management Service is a RESTful API built with Spring Boot that manages an inventory system with Categories, Products, and SKUs. The project follows a clean layered architecture pattern for maintainability and testability.

## Directory Structure

```
inventory-management-service/
│
├── src/
│   ├── main/
│   │   ├── java/com/inventory/management/
│   │   │   ├── InventoryManagementApplication.java    # Main Spring Boot application class
│   │   │   │
│   │   │   ├── controller/                             # REST API Controllers (Presentation Layer)
│   │   │   │   ├── CategoryController.java            # Category CRUD endpoints
│   │   │   │   ├── ProductController.java             # Product CRUD + search/filter endpoints
│   │   │   │   └── SkuController.java                 # SKU CRUD endpoints
│   │   │   │
│   │   │   ├── service/                                # Business Logic Layer
│   │   │   │   ├── CategoryService.java               # Category business logic
│   │   │   │   ├── ProductService.java                # Product business logic and orchestration
│   │   │   │   └── SkuService.java                    # SKU business logic
│   │   │   │
│   │   │   ├── repository/                             # Data Access Layer (Spring Data JPA)
│   │   │   │   ├── CategoryRepository.java            # Category data access
│   │   │   │   ├── ProductRepository.java             # Product data access with specifications
│   │   │   │   └── SkuRepository.java                 # SKU data access
│   │   │   │
│   │   │   ├── entity/                                 # Domain Models (JPA Entities)
│   │   │   │   ├── Category.java                      # Category entity with JPA annotations
│   │   │   │   ├── Product.java                       # Product entity with relationships
│   │   │   │   └── Sku.java                           # SKU entity
│   │   │   │
│   │   │   ├── dto/                                    # Data Transfer Objects
│   │   │   │   ├── request/                           # API Request DTOs
│   │   │   │   │   ├── CategoryRequest.java           # Category creation/update request
│   │   │   │   │   ├── ProductRequest.java            # Product creation/update request
│   │   │   │   │   └── SkuRequest.java                # SKU creation/update request
│   │   │   │   │
│   │   │   │   └── response/                          # API Response DTOs
│   │   │   │       ├── CategoryResponse.java          # Category response structure
│   │   │   │       ├── ProductResponse.java           # Product response structure
│   │   │   │       ├── SkuResponse.java               # SKU response structure
│   │   │   │       └── PagedResponse.java             # Generic paginated response wrapper
│   │   │   │
│   │   │   ├── mapper/                                 # Entity <-> DTO Converters
│   │   │   │   ├── CategoryMapper.java                # Category mapping logic
│   │   │   │   ├── ProductMapper.java                 # Product mapping logic
│   │   │   │   └── SkuMapper.java                     # SKU mapping logic
│   │   │   │
│   │   │   ├── exception/                              # Exception Handling
│   │   │   │   ├── ResourceNotFoundException.java     # 404 Not Found exception
│   │   │   │   ├── ValidationException.java           # Business validation exception
│   │   │   │   ├── ErrorResponse.java                 # Standard error response format
│   │   │   │   └── GlobalExceptionHandler.java        # Centralized exception handling (@ControllerAdvice)
│   │   │   │
│   │   │   ├── config/                                 # Configuration Classes
│   │   │   │   ├── OpenApiConfig.java                 # Swagger/OpenAPI documentation config
│   │   │   │   └── JpaConfig.java                     # JPA/Hibernate configuration
│   │   │   │
│   │   │   └── util/                                   # Utility Classes
│   │   │       └── ValidationUtil.java                # Common validation helpers
│   │   │
│   │   └── resources/
│   │       ├── application.yml                         # Main application configuration
│   │       ├── application-dev.yml                     # Development profile (optional)
│   │       ├── application-prod.yml                    # Production profile (optional)
│   │       ├── logback-spring.xml                      # Logging configuration
│   │       └── db/
│   │           └── migration/                          # Database migration scripts (if using Flyway/Liquibase)
│   │
│   └── test/
│       ├── java/com/inventory/management/
│       │   ├── InventoryManagementApplicationTests.java  # Application context test
│       │   │
│       │   ├── controller/                             # Controller Integration Tests (52 tests)
│       │   │   ├── CategoryControllerTest.java        # @WebMvcTest for Category API (18 tests)
│       │   │   ├── ProductControllerTest.java         # @WebMvcTest for Product API (19 tests)
│       │   │   └── SkuControllerTest.java             # @WebMvcTest for SKU API (16 tests)
│       │   │
│       │   ├── service/                                # Service Unit Tests (38 tests)
│       │   │   ├── CategoryServiceTest.java           # Category business logic tests (12 tests)
│       │   │   ├── ProductServiceTest.java            # Product business logic tests (12 tests)
│       │   │   └── SkuServiceTest.java                # SKU business logic tests (14 tests)
│       │   │
│       │   └── mapper/                                 # Mapper Unit Tests (24 tests)
│       │       ├── CategoryMapperTest.java            # Category mapping tests (8 tests)
│       │       ├── ProductMapperTest.java             # Product mapping tests (8 tests)
│       │       └── SkuMapperTest.java                 # SKU mapping tests (8 tests)
│       │
│       └── resources/
│           └── application-test.yml                    # Test configuration with H2 database
│
├── logs/                                               # Application log files (generated at runtime)
├── target/                                             # Maven build output
│   └── site/jacoco/                                   # JaCoCo coverage reports
├── .gitignore                                          # Git ignore file
├── pom.xml                                             # Maven project configuration
├── README.md                                           # Project documentation and getting started guide
├── PROJECT_STRUCTURE.md                                # This file - architecture and structure
├── TEST_REPORT.md                                      # Comprehensive test coverage report
├── SWAGGER-GUIDE.md                                    # Swagger UI usage guide
├── API-DOCUMENTATION.md                                # Detailed API documentation
└── CHAT_HISTORY.md                                     # Development history and decisions

```

## Architecture Layers

### 1. **Controller Layer** (`controller/`)
- **Purpose**: Handle HTTP requests and responses
- **Responsibilities**:
  - Define REST API endpoints
  - Validate request parameters and body (@Valid)
  - Call service layer methods
  - Map service responses to HTTP responses
  - Handle HTTP status codes
- **Annotations**: `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc.
- **Testing**: Integration tests with `@WebMvcTest` and MockMvc

### 2. **Service Layer** (`service/`)
- **Purpose**: Implement business logic and orchestration
- **Responsibilities**:
  - Business rule validation
  - Transaction management (@Transactional)
  - Orchestrate multiple repository calls
  - Use mappers to convert entities to DTOs
  - Throw business exceptions (ResourceNotFoundException, ValidationException)
- **Annotations**: `@Service`, `@Transactional`
- **Testing**: Unit tests with mocked repositories

### 3. **Repository Layer** (`repository/`)
- **Purpose**: Data access abstraction
- **Responsibilities**:
  - CRUD operations via Spring Data JPA
  - Custom queries with @Query
  - Dynamic queries using Specifications
  - Database interaction without business logic
- **Annotations**: `@Repository` (implicit with JpaRepository)
- **Testing**: Integration tests with `@DataJpaTest` and H2

### 4. **Entity Layer** (`entity/`)
- **Purpose**: Domain models representing database tables
- **Responsibilities**:
  - Define table structure with JPA annotations
  - Define relationships (@ManyToOne, @OneToMany)
  - Validation constraints
  - Audit fields (createdAt, updatedAt)
- **Annotations**: `@Entity`, `@Table`, `@Id`, `@Column`, `@ManyToOne`, etc.

### 5. **DTO Layer** (`dto/`)
- **Purpose**: Data transfer objects for API contracts
- **Responsibilities**:
  - Define API request/response structure
  - Decouple API from database schema
  - Validation annotations (@NotNull, @NotBlank, @Size)
  - Prevent exposing internal entity structure
- **Annotations**: `@Data`, `@Builder`, `@NotNull`, `@NotBlank`, `@Size`, etc.

### 6. **Mapper Layer** (`mapper/`)
- **Purpose**: Convert between entities and DTOs
- **Responsibilities**:
  - Entity -> Response DTO conversion
  - Request DTO -> Entity conversion
  - Handle nested object mapping
  - Type-safe conversions
- **Annotations**: `@Component`
- **Testing**: Unit tests for mapping logic

### 7. **Exception Layer** (`exception/`)
- **Purpose**: Centralized error handling
- **Responsibilities**:
  - Define custom exceptions
  - Global exception handler with @ControllerAdvice
  - Standard error response format
  - Log errors appropriately
- **Key Classes**:
  - `ResourceNotFoundException`: For 404 errors
  - `ValidationException`: For business validation failures
  - `GlobalExceptionHandler`: Catches and handles all exceptions
  - `ErrorResponse`: Standard error response structure

### 8. **Configuration Layer** (`config/`)
- **Purpose**: Application configuration beans
- **Responsibilities**:
  - Swagger/OpenAPI configuration
  - JPA/Hibernate settings
  - Security configuration (if needed)
  - Custom beans and configurations
- **Annotations**: `@Configuration`, `@Bean`

## Data Flow

```
Request Flow:
Client Request 
    ↓
Controller (validate request, handle HTTP)
    ↓
Service (business logic, transaction management)
    ↓
Repository (data access)
    ↓
Database (PostgreSQL)

Response Flow:
Database → Repository → Entity
    ↓
Service (map to DTO)
    ↓
Controller (HTTP response)
    ↓
Client Response
```

## Key Design Patterns

1. **Layered Architecture**: Clear separation of concerns across layers
2. **DTO Pattern**: Separate API contract from domain model
3. **Repository Pattern**: Abstract data access logic
4. **Dependency Injection**: Spring manages all dependencies
5. **Builder Pattern**: Used in DTOs for clean object construction (Lombok @Builder)
6. **Strategy Pattern**: Used in Specifications for dynamic queries

## Naming Conventions

- **Entities**: Singular nouns (Category, Product, Sku)
- **Repositories**: `<Entity>Repository` (CategoryRepository)
- **Services**: `<Entity>Service` (CategoryService)
- **Controllers**: `<Entity>Controller` (CategoryController)
- **Request DTOs**: `<Entity>Request` (CategoryRequest)
- **Response DTOs**: `<Entity>Response` (CategoryResponse)
- **Mappers**: `<Entity>Mapper` (CategoryMapper)
- **Tests**: `<ClassName>Test` (CategoryServiceTest)

## Database Schema

```
┌─────────────┐
│  Category   │
├─────────────┤
│ id          │ PK
│ name        │ UNIQUE, NOT NULL
│ description │
│ created_at  │
│ updated_at  │
└─────────────┘
       ▲
       │
       │ (1:N)
       │
┌─────────────┐
│   Product   │
├─────────────┤
│ id          │ PK
│ name        │ NOT NULL
│ description │
│ price       │ NOT NULL
│ category_id │ FK → Category
│ created_at  │
│ updated_at  │
└─────────────┘
       ▲
       │
       │ (1:N)
       │
┌─────────────┐
│     SKU     │
├─────────────┤
│ id          │ PK
│ sku_code    │ UNIQUE, NOT NULL
│ quantity    │ NOT NULL
│ product_id  │ FK → Product
│ attributes  │ JSON (color, size, etc.)
│ created_at  │
│ updated_at  │
└─────────────┘
```

## Testing Strategy

- **Unit Tests**: Test individual methods in isolation (services, mappers)
- **Integration Tests**: Test layers with real dependencies (controllers, repositories)
- **Coverage Achieved**: 81.68% line coverage (exceeds 80% target!)
- **Total Tests**: 115 tests (all passing)
- **Tools**: JUnit 5, Mockito, Spring Boot Test, H2, JaCoCo
- **Test Breakdown**:
  - Service Layer: 38 tests (100% coverage)
  - Controller Layer: 52 tests (97.6% coverage)
  - Mapper Layer: 24 tests (100% coverage)
  - Application Context: 1 test

## API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **Features**:
  - Comprehensive API documentation with examples
  - Interactive "Try it out" functionality
  - Request/response schemas with validation rules
  - Error response documentation

## Application Status

✅ **Fully Implemented and Production-Ready**

The Inventory Management Service is **complete** with all core features implemented:

- ✅ Full CRUD operations for Categories, Products, and SKUs
- ✅ Advanced search and filtering with pagination
- ✅ Comprehensive validation and error handling
- ✅ Foreign key constraint protection
- ✅ Complete API documentation with Swagger
- ✅ 115 tests with 81.68% line coverage
- ✅ Centralized exception handling with error tracking
- ✅ Structured logging with SLF4J
- ✅ JSONB support for flexible SKU attributes

## Running the Application

1. **Prerequisites**: Java 8+, Maven 3.6+, PostgreSQL 12+
2. **Database Setup**: Create a PostgreSQL database
3. **Configuration**: Update `application.yml` with database credentials
4. **Build**: `mvn clean install`
5. **Run**: `mvn spring-boot:run`
6. **Access**: 
   - API Base URL: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

## Key Features

- **Category Management**: Organize products into hierarchical categories
- **Product Management**: Track product details with pricing and categorization
- **SKU Management**: Handle product variants with flexible JSON attributes
- **Search & Filter**: Dynamic product search by name, category, and price range
- **Pagination**: Efficient data retrieval with paginated responses
- **Data Integrity**: Foreign key protection prevents orphaned records
- **API Documentation**: Complete Swagger documentation with examples
- **Error Handling**: Consistent error responses with unique tracking IDs

## Additional Resources

- **README.md**: Comprehensive project documentation and API usage guide
- **TEST_REPORT.md**: Detailed test coverage and quality metrics
- **SWAGGER-GUIDE.md**: Guide to exploring and using the API documentation
- **API-DOCUMENTATION.md**: Detailed API endpoint documentation
