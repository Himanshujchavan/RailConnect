package com.railconnect.common.constants;

public final class SecurityConstants {

    private SecurityConstants() {
        // Prevent instantiation
    }

    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_PREFIX = "Bearer ";
    
    // 24 Hours validity token window
    public static final long JWT_EXPIRATION_MS = 86400000L;
    
    // 2 Hours validity password reset link window
    public static final long PASSWORD_RESET_EXPIRATION_MS = 7200000L;
}
