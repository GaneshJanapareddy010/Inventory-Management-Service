# Accessing API Documentation - Quick Start Guide

## 🚀 Starting the Application

Run the inventory management service:

```bash
mvn spring-boot:run
```

Wait for the application to start (you'll see `Started InventoryManagementApplication` in the logs).

---

## 📚 Accessing Swagger UI

Once the application is running, open your browser:

### **Swagger UI (Interactive Documentation)**
```
http://localhost:8080/swagger-ui.html
```

### What You'll See:
- ✅ **Interactive API Explorer** - Try out every endpoint directly from your browser
- ✅ **Request/Response Examples** - See real JSON examples for all operations
- ✅ **Field Validation Rules** - Understand exactly what's required
- ✅ **Error Documentation** - Know what errors to expect and how to handle them

---

## 📋 Quick Tour of the Swagger UI

### 1. **Category APIs** (Top Level)
- Create categories like "Electronics", "Apparel", "Food"
- View, update, and delete categories
- Try it: Click "POST /categories" → "Try it out" → Use example payload

### 2. **Product APIs** (Items within categories)
- Create products with prices and descriptions
- Search/filter products by name, category, price range
- Pagination support for large datasets
- Try it: Click "GET /products" → "Try it out" → Enter search terms

### 3. **SKU APIs** (Product Variants)
- Create SKUs with flexible JSON attributes
- Store variant details like color, size, storage, etc.
- List all SKUs for a specific product
- Try it: Click "POST /skus" → "Try it out" → See JSONB attributes example

---

## 🧪 Testing APIs in Swagger UI

### Example 1: Create a Category
1. Expand **POST /categories**
2. Click **"Try it out"**
3. The example JSON will populate automatically:
   ```json
   {
     "name": "Electronics",
     "description": "Electronic devices and accessories"
   }
   ```
4. Click **"Execute"**
5. View the response below (should be 201 Created)

### Example 2: Search Products
1. Expand **GET /products**
2. Click **"Try it out"**
3. Fill in parameters:
   - `search`: `laptop`
   - `categoryId`: `1`
   - `minPrice`: `1000`
   - `maxPrice`: `3000`
4. Click **"Execute"**
5. See paginated results

### Example 3: Create SKU with Attributes
1. Expand **POST /skus**
2. Click **"Try it out"**
3. The example shows flexible JSONB attributes:
   ```json
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
4. Click **"Execute"**

---

## 📖 OpenAPI Specification (For Programmatic Access)

### JSON Format
```
http://localhost:8080/v3/api-docs
```

### YAML Format
```
http://localhost:8080/v3/api-docs.yaml
```

### Use Cases:
- Import into **Postman** (File → Import → Link)
- Generate client code using **OpenAPI Generator**
- API documentation tools like **Redoc** or **ReDoc**
- Integration testing frameworks

---

## 🔍 Key Features Documented

### Field Validation
Every field shows:
- **Required** vs **Optional**
- **Min/Max Length** (e.g., category name: 2-100 characters)
- **Data Type** (string, number, object)
- **Constraints** (e.g., price must be > 0)
- **Examples** (realistic sample values)

### Error Responses
All endpoints document:
- **400 Bad Request** - Validation errors with field-level details
- **404 Not Found** - Resource doesn't exist
- **500 Internal Server Error** - Unexpected errors

### Error Response Format
```json
{
  "timestamp": "2026-01-04T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
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

The `errorId` helps track errors in server logs.

---

## 📦 Example Workflow

### Complete Product Hierarchy Creation

1. **Create Category** (POST /categories)
   ```json
   {"name": "Electronics", "description": "Electronic devices"}
   ```
   → Response: `{"id": 1, ...}`

2. **Create Product** (POST /products)
   ```json
   {
     "name": "MacBook Pro 16-inch",
     "description": "High-performance laptop",
     "price": 2499.99,
     "categoryId": 1
   }
   ```
   → Response: `{"id": 1, "categoryId": 1, ...}`

3. **Create SKUs** (POST /skus) - Multiple variants
   
   **Variant 1 - Space Gray, 512GB:**
   ```json
   {
     "skuCode": "MBP16-SG-512",
     "productId": 1,
     "attributes": {"color": "Space Gray", "storage": "512GB", "ram": "18GB"}
   }
   ```
   
   **Variant 2 - Silver, 1TB:**
   ```json
   {
     "skuCode": "MBP16-SL-1TB",
     "productId": 1,
     "attributes": {"color": "Silver", "storage": "1TB", "ram": "36GB"}
   }
   ```

4. **List Product SKUs** (GET /skus?productId=1)
   → See all variants

5. **Search Products** (GET /products?search=MacBook&categoryId=1)
   → Find products by keyword

---

## 🎯 Search & Filter Capabilities

### Product Search Parameters:
- **search** - Keyword search in product name/description
- **categoryId** - Filter by category
- **minPrice** / **maxPrice** - Price range
- **page** - Page number (0-indexed)
- **size** - Items per page (default: 20)
- **sortBy** - Field to sort (name, price, createdAt)
- **sortDir** - Sort direction (asc, desc)

### Example: Find laptops under $2000
```
GET /products?search=laptop&maxPrice=2000&sortBy=price&sortDir=asc
```

---

## 🛠️ Additional Tools

### Using cURL
```bash
# Create category
curl -X POST http://localhost:8080/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Electronics", "description": "Electronic devices"}'

# Get product
curl http://localhost:8080/products/1

# Search products
curl "http://localhost:8080/products?search=laptop&page=0&size=10"
```

### Using Postman
1. Open Postman
2. Click **Import**
3. Select **Link**
4. Enter: `http://localhost:8080/v3/api-docs`
5. Click **Continue** and **Import**
6. All endpoints will be organized under folders:
   - Category
   - Product
   - SKU Management

---

## ❓ Troubleshooting

### Cannot Access Swagger UI

**Problem:** Browser shows "This site can't be reached"

**Solutions:**
1. Check if application is running:
   ```bash
   # Look for this log line:
   Started InventoryManagementApplication in X.XXX seconds
   ```

2. Verify port 8080 is not in use:
   ```powershell
   netstat -ano | findstr :8080
   ```

3. Check application.yml for custom port:
   ```yaml
   server:
     port: 8080  # Should be 8080
   ```

### Documentation Not Loading

**Problem:** Page loads but API endpoints don't appear

**Solutions:**
1. Clear browser cache (Ctrl + Shift + Delete)
2. Try incognito/private mode
3. Check browser console for errors (F12)
4. Verify URL: `http://localhost:8080/swagger-ui.html` (no HTTPS)

### "Try it out" Returns 404

**Problem:** Endpoint returns 404 when testing

**Solutions:**
1. Check if you created the required resources first
   - Products require a valid `categoryId`
   - SKUs require a valid `productId`
2. Verify IDs in your request match existing resources
3. Check the response - the error message will indicate what's missing

---

## 📞 Support

For issues or questions:
- Check server logs in the terminal
- Use the `errorId` from error responses to search logs
- Review [API-DOCUMENTATION.md](API-DOCUMENTATION.md) for detailed specs
- All endpoints return descriptive error messages

---

## 📌 Quick Reference

| Documentation Type | URL | Purpose |
|--------------------|-----|---------|
| Swagger UI | http://localhost:8080/swagger-ui.html | Interactive testing |
| OpenAPI JSON | http://localhost:8080/v3/api-docs | Programmatic access |
| OpenAPI YAML | http://localhost:8080/v3/api-docs.yaml | Import to tools |
| H2 Console | http://localhost:8080/h2-console | Database inspection |

**H2 Database Connection (for testing):**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: *(leave blank)*

---

**Happy API Testing! 🎉**

For detailed API specifications, see [API-DOCUMENTATION.md](API-DOCUMENTATION.md)
