package com.railconnect.dashboard.controller;

import com.railconnect.booking.dtorequestresponse.PassengerRequest;
import com.railconnect.dashboard.dtorequestresponse.DashboardBookingResponse;
import com.railconnect.dashboard.dtorequestresponse.FavouriteRouteRequest;
import com.railconnect.dashboard.dtorequestresponse.FavouriteRouteResponse;
import com.railconnect.dashboard.service.DashboardService;
import com.railconnect.notification.dtorequestresponse.NotificationResponse;
import com.railconnect.user.dtorequestresponse.PassengerResponse;
import com.railconnect.user.dtorequestresponse.UpdateProfileRequest;
import com.railconnect.user.dtorequestresponse.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Phase 7 — User Dashboard.
 * <p>
 * Everything here is scoped to a single user via the {@code userId} path segment, matching the
 * userId-parameterized convention already used by {@code NotificationController} and
 * {@code SearchController} in this codebase.
 */
@RestController
@RequestMapping("/api/v1/dashboard/{userId}")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // --- Profile ---

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(dashboardService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@PathVariable Long userId,
                                                       @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(dashboardService.updateProfile(userId, request));
    }

    // --- Saved Passengers ---

    @GetMapping("/passengers")
    public ResponseEntity<List<PassengerResponse>> getSavedPassengers(@PathVariable Long userId) {
        return ResponseEntity.ok(dashboardService.getSavedPassengers(userId));
    }

    @PostMapping("/passengers")
    public ResponseEntity<PassengerResponse> addSavedPassenger(@PathVariable Long userId,
                                                                @Valid @RequestBody PassengerRequest request) {
        return new ResponseEntity<>(dashboardService.addSavedPassenger(userId, request), HttpStatus.CREATED);
    }

    @PutMapping("/passengers/{passengerId}")
    public ResponseEntity<PassengerResponse> updateSavedPassenger(@PathVariable Long userId,
                                                                   @PathVariable Long passengerId,
                                                                   @Valid @RequestBody PassengerRequest request) {
        return ResponseEntity.ok(dashboardService.updateSavedPassenger(userId, passengerId, request));
    }

    @DeleteMapping("/passengers/{passengerId}")
    public ResponseEntity<Void> deleteSavedPassenger(@PathVariable Long userId, @PathVariable Long passengerId) {
        dashboardService.deleteSavedPassenger(userId, passengerId);
        return ResponseEntity.noContent().build();
    }

    // --- Trips ---

    @GetMapping("/bookings")
    public ResponseEntity<List<DashboardBookingResponse>> getBookingHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(dashboardService.getBookingHistory(userId));
    }

    @GetMapping("/bookings/upcoming")
    public ResponseEntity<List<DashboardBookingResponse>> getUpcomingTrips(@PathVariable Long userId) {
        return ResponseEntity.ok(dashboardService.getUpcomingTrips(userId));
    }

    @GetMapping("/bookings/cancelled")
    public ResponseEntity<List<DashboardBookingResponse>> getCancelledTrips(@PathVariable Long userId) {
        return ResponseEntity.ok(dashboardService.getCancelledTrips(userId));
    }

    // --- Favourite Routes ---

    @GetMapping("/favourite-routes")
    public ResponseEntity<List<FavouriteRouteResponse>> getFavouriteRoutes(@PathVariable Long userId) {
        return ResponseEntity.ok(dashboardService.getFavouriteRoutes(userId));
    }

    @PostMapping("/favourite-routes")
    public ResponseEntity<FavouriteRouteResponse> addFavouriteRoute(@PathVariable Long userId,
                                                                     @Valid @RequestBody FavouriteRouteRequest request) {
        return new ResponseEntity<>(dashboardService.addFavouriteRoute(userId, request), HttpStatus.CREATED);
    }

    @DeleteMapping("/favourite-routes/{favouriteRouteId}")
    public ResponseEntity<Void> removeFavouriteRoute(@PathVariable Long userId, @PathVariable Long favouriteRouteId) {
        dashboardService.removeFavouriteRoute(userId, favouriteRouteId);
        return ResponseEntity.noContent().build();
    }

    // --- Notifications ---

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(dashboardService.getNotifications(userId));
    }
}
