package com.inventory.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application class for Inventory Management Service.
 * 
 * This application provides REST APIs for managing:
 * - Categories
 * - Products
 * - SKUs
 * 
 * @author Inventory Team
 * @version 1.0.0
 */
@SpringBootApplication
public class InventoryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryManagementApplication.class, args);
    }
}
