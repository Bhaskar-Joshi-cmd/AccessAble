package com.accessable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the AccessAble backend.
 *
 * Run this class to start the Spring Boot application on port 8080.
 * The real database connection details are read from environment variables
 * (DB_URL, DB_USERNAME, DB_PASSWORD), never from this file.
 */
@SpringBootApplication
public class AccessAbleApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccessAbleApplication.class, args);
    }
}