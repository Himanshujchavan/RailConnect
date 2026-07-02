package com.railconnect.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordUtil {

    private static final BCryptPasswordEncoder DELEGATE = new BCryptPasswordEncoder();

    private PasswordUtil() {
        // Prevent instantiation
    }

    /**
     * Hashes plain-text passwords securely using standard random salt BCrypt configurations.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        return DELEGATE.encode(plainPassword);
    }

    /**
     * Checks if a raw password matches an existing salted password hash database entry string.
     */
    public static boolean matches(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return DELEGATE.matches(plainPassword, hashedPassword);
    }
}