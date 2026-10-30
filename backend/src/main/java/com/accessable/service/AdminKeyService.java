package com.accessable.service;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.Optional;

/**
 * Simple shared-secret validation for administrator endpoints.
 * Reads the expected key from the {@code admin-key} environment variable.
 */
@Service
public class AdminKeyService {

    private String expectedKey;

    @PostConstruct
    public void init() {
        String key = System.getenv("ADMIN_KEY");
        if (key == null || key.isBlank()) {
            this.expectedKey = "";
        } else {
            this.expectedKey = key;
        }
    }

    /**
     * Returns true if the provided key matches the expected key.
     * Access is denied if the expected key is blank (fail-closed).
     */
    public boolean validate(String requestKey) {
        if (expectedKey.isBlank()) {
            return false;
        }
        return expectedKey.equals(requestKey);
    }
}