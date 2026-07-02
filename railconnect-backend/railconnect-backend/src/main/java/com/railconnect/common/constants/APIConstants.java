package com.railconnect.common.constants;

public final class APIConstants {

    private APIConstants() {
        // Prevent instantiation
    }

    public static final String API_V1_PREFIX = "/api/v1";

    public static final String AUTH_API = API_V1_PREFIX + "/auth";
    public static final String STATIONS_API = API_V1_PREFIX + "/stations";
    public static final String TRAINS_API = API_V1_PREFIX + "/trains";
    public static final String RESERVATIONS_API = API_V1_PREFIX + "/reservations";
    public static final String PAYMENTS_API = API_V1_PREFIX + "/payments";
    public static final String NOTIFICATIONS_API = API_V1_PREFIX + "/notifications";
}
