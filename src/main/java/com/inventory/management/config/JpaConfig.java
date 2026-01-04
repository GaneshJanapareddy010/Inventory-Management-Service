package com.inventory.management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA configuration for the application.
 * Enables JPA repositories, auditing, and transaction management.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.inventory.management.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
    // Additional JPA configurations can be added here if needed
}
