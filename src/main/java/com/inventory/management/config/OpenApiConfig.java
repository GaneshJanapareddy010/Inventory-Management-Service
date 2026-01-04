package com.inventory.management.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuration for OpenAPI/Swagger documentation.
 * 
 * Access the API documentation at:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI inventoryManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventory Management Service API")
                        .description("Comprehensive REST API for managing inventory system with hierarchical data model:\n\n" +
                                "- **Categories**: Top-level organization (e.g., Electronics, Apparel)\n" +
                                "- **Products**: Items within categories (e.g., Laptop, Smartphone)\n" +
                                "- **SKUs**: Specific variants with unique attributes (e.g., Laptop-Silver-512GB)\n\n" +
                                "### Features\n" +
                                "- Full CRUD operations for all entities\n" +
                                "- Advanced search and filtering\n" +
                                "- Pagination and sorting support\n" +
                                "- Comprehensive validation\n" +
                                "- Consistent error handling\n" +
                                "- JSONB attribute storage for flexible SKU properties")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Inventory Management Team")
                                .email("inventory-support@example.com")
                                .url("https://github.com/inventory-management"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(Arrays.asList(
                        new Server().url("http://localhost:" + serverPort).description("Local Development Server"),
                        new Server().url("https://api.inventory.example.com").description("Production Server")))
                .components(new Components()
                        .addSchemas("ErrorResponse", createErrorResponseSchema())
                        .addResponses("NotFound", create404Response())
                        .addResponses("BadRequest", create400Response())
                        .addResponses("InternalServerError", create500Response()));
    }

    private Schema createErrorResponseSchema() {
        return new Schema()
                .type("object")
                .description("Standard error response structure")
                .addProperty("timestamp", new Schema().type("string").format("date-time").example("2026-01-04T10:30:00"))
                .addProperty("status", new Schema().type("integer").example(400))
                .addProperty("error", new Schema().type("string").example("Bad Request"))
                .addProperty("message", new Schema().type("string").example("Validation failed for one or more fields"))
                .addProperty("path", new Schema().type("string").example("/products"))
                .addProperty("errorId", new Schema().type("string").format("uuid").example("a1b2c3d4-e5f6-7890-abcd-ef1234567890"))
                .addProperty("validationErrors", new Schema()
                        .type("array")
                        .items(new Schema()
                                .type("object")
                                .addProperty("field", new Schema().type("string").example("name"))
                                .addProperty("message", new Schema().type("string").example("must not be blank"))));
    }

    private ApiResponse create404Response() {
        return new ApiResponse()
                .description("Resource not found")
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema().$ref("#/components/schemas/ErrorResponse"))
                                .example(createExample(404, "Not Found", "Product not found with id: '999'"))));
    }

    private ApiResponse create400Response() {
        return new ApiResponse()
                .description("Bad request - validation error")
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema().$ref("#/components/schemas/ErrorResponse"))
                                .example(createExample(400, "Bad Request", "Validation failed for one or more fields"))));
    }

    private ApiResponse create500Response() {
        return new ApiResponse()
                .description("Internal server error")
                .content(new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema().$ref("#/components/schemas/ErrorResponse"))
                                .example(createExample(500, "Internal Server Error", "An unexpected error occurred. Please contact support with error ID: ..."))));
    }

    private Object createExample(int status, String error, String message) {
        return "{\n" +
                "  \"timestamp\": \"2026-01-04T10:30:00\",\n" +
                "  \"status\": " + status + ",\n" +
                "  \"error\": \"" + error + "\",\n" +
                "  \"message\": \"" + message + "\",\n" +
                "  \"path\": \"/products/999\",\n" +
                "  \"errorId\": \"a1b2c3d4-e5f6-7890-abcd-ef1234567890\"\n" +
                "}";
    }
}
