package com.railconnect.dashboard.service;

import com.railconnect.booking.dtorequestresponse.PassengerRequest;
import com.railconnect.dashboard.dtorequestresponse.DashboardBookingResponse;
import com.railconnect.dashboard.dtorequestresponse.FavouriteRouteRequest;
import com.railconnect.dashboard.dtorequestresponse.FavouriteRouteResponse;
import com.railconnect.notification.dtorequestresponse.NotificationResponse;
import com.railconnect.user.dtorequestresponse.PassengerResponse;
import com.railconnect.user.dtorequestresponse.UpdateProfileRequest;
import com.railconnect.user.dtorequestresponse.UserResponse;

import java.util.List;

/**
 * Phase 7 — User Dashboard.
 * <p>
 * A read/write facade over the account-centric views a logged-in user sees: their profile,
 * saved passengers, booking history (all/upcoming/cancelled), favourite routes, and
 * notifications. Deliberately thin - it composes existing repositories/services
 * (e.g. {@link com.railconnect.notification.service.NotificationService}) rather than
 * duplicating logic those modules already own.
 */
public interface DashboardService {

    // --- Profile ---
    UserResponse getProfile(Long userId);
    UserResponse updateProfile(Long userId, UpdateProfileRequest request);

    // --- Saved Passengers ---
    List<PassengerResponse> getSavedPassengers(Long userId);
    PassengerResponse addSavedPassenger(Long userId, PassengerRequest request);
    PassengerResponse updateSavedPassenger(Long userId, Long passengerId, PassengerRequest request);
    void deleteSavedPassenger(Long userId, Long passengerId);

    // --- Trips ---
    List<DashboardBookingResponse> getBookingHistory(Long userId);
    List<DashboardBookingResponse> getUpcomingTrips(Long userId);
    List<DashboardBookingResponse> getCancelledTrips(Long userId);

    // --- Favourite Routes ---
    List<FavouriteRouteResponse> getFavouriteRoutes(Long userId);
    FavouriteRouteResponse addFavouriteRoute(Long userId, FavouriteRouteRequest request);
    void removeFavouriteRoute(Long userId, Long favouriteRouteId);

    // --- Notifications ---
    List<NotificationResponse> getNotifications(Long userId);
}
