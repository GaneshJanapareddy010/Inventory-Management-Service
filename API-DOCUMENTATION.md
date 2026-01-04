# API Documentation - Inventory Management Service

## Accessing the API Documentation

### Swagger UI (Interactive Documentation)
Once the application is running, access the interactive API documentation at:

**URL:** http://localhost:8080/swagger-ui.html

**Features:**
- ✅ Interactive API explorer - Try out endpoints directly from the browser
- ✅ Complete request/response schemas with examples
- ✅ Field-level validation requirements
- ✅ Error response documentation
- ✅ Authentication testing (if enabled)

### OpenAPI JSON Specification
For programmatic access or importing into tools like Postman:

**URL:** http://localhost:8080/v3/api-docs

**Formats Available:**
- JSON: http://localhost:8080/v3/api-docs
- YAML: http://localhost:8080/v3/api-docs.yaml

---

## API Overview

### Base Information
- **Version:** 1.0.0
- **Base URL:** http://localhost:8080
- **Content-Type:** application/json
- **License:** Apache 2.0

### Resource Hierarchy
```
Categories (Top Level)
  └── Products (Items within categories)
      └── SKUs (Specific variants with attributes)
```

---

## Available Endpoints

### 📁 Categories
Manage product categories (e.g., Electronics, Apparel)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/categories` | Create a new category |
| GET | `/categories/{id}` | Get category by ID |
| GET | `/categories` | List all categories |
| PUT | `/categories/{id}` | Update category |
| DELETE | `/categories/{id}` | Delete category |

**Example Request (Create Category):**
```json
POST /categories
{
  "name": "Electronics",
  "description": "Electronic devices and accessories"
}
```

**Example Response:**
```json
{
  "id": 1,
  "name": "Electronics",
  "description": "Electronic devices and accessories",
  "createdAt": "2026-01-04T10:30:00",
  "updatedAt": "2026-01-04T10:30:00"
}
```

---

### 📦 Products
Manage products within categories

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/products` | Create a new product |
| GET | `/products/{id}` | Get product by ID |
| GET | `/products?search=&categoryId=&minPrice=&maxPrice=&page=&size=` | Search/filter products with pagination |
| PUT | `/products/{id}` | Update product |
| DELETE | `/products/{id}` | Delete product |

**Example Request (Create Product):**
```json
POST /products
{
  "name": "MacBook Pro 16-inch",
  "description": "High-performance laptop with M3 Pro chip, 18GB RAM, and 512GB SSD",
  "price": 2499.99,
  "categoryId": 1
}
```

**Example Response:**
```json
{
  "id": 1,
  "name": "MacBook Pro 16-inch",
  "description": "High-performance laptop with M3 Pro chip, 18GB RAM, and 512GB SSD",
  "price": 2499.99,
  "categoryId": 1,
  "categoryName": "Electronics",
  "createdAt": "2026-01-04T10:30:00",
  "updatedAt": "2026-01-04T10:30:00"
}
```

**Search/Filter Examples:**
- Search by name: `GET /products?search=MacBook`
- Filter by category: `GET /products?categoryId=1`
- Price range: `GET /products?minPrice=1000&maxPrice=3000`
- Pagination: `GET /products?page=0&size=10&sortBy=price&sortDir=desc`
- Combined: `GET /products?search=laptop&categoryId=1&minPrice=1000&page=0&size=20`

---

### 🏷️ SKUs (Stock Keeping Units)
Manage product variants with flexible attributes

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/skus` | Create a new SKU |
| GET | `/skus/{id}` | Get SKU by ID |
| GET | `/skus?productId={id}` | List all SKUs for a product |
| PUT | `/skus/{id}` | Update SKU |
| DELETE | `/skus/{id}` | Delete SKU |

**Example Request (Create SKU):**
```json
POST /skus
{
  "skuCode": "MBP16-SG-512",
  "productId": 1,
  "attributes": {
    "color": "Space Gray",
    "storage": "512GB",
    "ram": "18GB",
    "warranty": "3 years"
  }
}
```

**Example Response:**
```json
{
  "id": 1,
  "skuCode": "MBP16-SG-512",
  "productId": 1,
  "productName": "MacBook Pro 16-inch",
  "attributes": {
    "color": "Space Gray",
    "storage": "512GB",
    "ram": "18GB",
    "warranty": "3 years"
  },
  "createdAt": "2026-01-04T10:30:00",
  "updatedAt": "2026-01-04T10:30:00"
}
```

---

## Error Responses

All errors follow a consistent format with unique error IDs for tracking:

### Error Response Structure
```json
{
  "timestamp": "2026-01-04T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "path": "/products",
  "errorId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "validationErrors": [
    {
      "field": "price",
      "message": "must be greater than 0"
    }
  ]
}
```

### HTTP Status Codes

| Code | Status | Description | Example |
|------|--------|-------------|---------|
| 200 | OK | Request succeeded | GET /products/1 |
| 201 | Created | Resource created | POST /products |
| 204 | No Content | Delete succeeded | DELETE /products/1 |
| 400 | Bad Request | Validation error or malformed request | Invalid JSON, missing required fields |
| 404 | Not Found | Resource doesn't exist | Product with ID 999 not found |
| 405 | Method Not Allowed | HTTP method not supported | PATCH /products/1 |
| 500 | Internal Server Error | Unexpected server error | Database connection failed |

### Common Error Scenarios

**1. Validation Error (400)**
```json
{
  "timestamp": "2026-01-04T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "path": "/products",
  "errorId": "uuid-here",
  "validationErrors": [
    {
      "field": "name",
      "message": "must not be blank"
    },
    {
      "field": "price",
      "message": "must be greater than 0"
    }
  ]
}
```

**2. Resource Not Found (404)**
```json
{
  "timestamp": "2026-01-04T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: '999'",
  "path": "/products/999",
  "errorId": "uuid-here"
}
```

**3. Duplicate Resource (400)**
```json
{
  "timestamp": "2026-01-04T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Category name already exists: Electronics",
  "path": "/categories",
  "errorId": "uuid-here"
}
```

**4. Malformed JSON (400)**
```json
{
  "timestamp": "2026-01-04T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Malformed JSON request. Please check your request body.",
  "path": "/products",
  "errorId": "uuid-here"
}
```

---

## Validation Rules

### Category
- **name**: Required, 2-100 characters, must be unique
- **description**: Optional, max 500 characters

### Product
- **name**: Required, 2-200 characters
- **description**: Optional, max 1000 characters
- **price**: Required, must be greater than 0
- **categoryId**: Required, category must exist

### SKU
- **skuCode**: Required, must be unique
- **productId**: Required, product must exist
- **attributes**: Optional, flexible JSON object

---

## Pagination

All list endpoints support pagination with the following parameters:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | integer | 0 | Page number (0-indexed) |
| size | integer | 20 | Items per page |
| sortBy | string | name | Field to sort by |
| sortDir | string | asc | Sort direction (asc/desc) |

**Pagination Response:**
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 45,
  "totalPages": 3,
  "first": true,
  "last": false,
  "empty": false
}
```

---

## Testing the API

### Using Swagger UI
1. Start the application: `mvn spring-boot:run`
2. Open browser: http://localhost:8080/swagger-ui.html
3. Expand any endpoint
4. Click "Try it out"
5. Fill in parameters/request body
6. Click "Execute"
7. View response below

### Using cURL

**Create Category:**
```bash
curl -X POST http://localhost:8080/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Electronics", "description": "Electronic devices"}'
```

**Get Product:**
```bash
curl -X GET http://localhost:8080/products/1
```

**Search Products:**
```bash
curl -X GET "http://localhost:8080/products?search=laptop&categoryId=1&page=0&size=10"
```

### Using Postman
1. Import OpenAPI spec: http://localhost:8080/v3/api-docs
2. Postman will auto-generate all requests
3. Organized by tags (Category, Product, SKU)

---

## Development Environment

### Starting the Application
```bash
# Using Maven
mvn spring-boot:run

# Or build and run JAR
mvn clean package
java -jar target/inventory-management-service-1.0.0-SNAPSHOT.jar
```

### Accessing Documentation
- Wait for application to fully start
- Look for log: `Started InventoryManagementApplication`
- Open browser to http://localhost:8080/swagger-ui.html

### Configuration
Edit `application.yml` to customize:
- Server port (default: 8080)
- Database connection
- Logging levels
- API documentation paths

---

## Support

For issues with the API documentation or endpoints:
1. Check the error ID in the response
2. Search application logs for that error ID
3. Contact: inventory-support@example.com
4. GitHub: https://github.com/inventory-management

---

**Last Updated:** January 4, 2026  
**Version:** 1.0.0  
**License:** Apache 2.0
