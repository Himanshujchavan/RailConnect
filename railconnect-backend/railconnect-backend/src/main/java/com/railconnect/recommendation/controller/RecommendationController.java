package com.railconnect.recommendation.controller;

import com.railconnect.dashboard.dtorequestresponse.FavouriteRouteResponse;
import com.railconnect.recommendation.dtorequestresponse.CoachRecommendationResponse;
import com.railconnect.recommendation.dtorequestresponse.FrequentRouteResponse;
import com.railconnect.recommendation.dtorequestresponse.SuggestedTrainResponse;
import com.railconnect.recommendation.service.RecommendationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Recommendation Engine. Scoped per user via the {@code userId} path segment, matching the
 * convention {@code DashboardController}/{@code NotificationController} already use.
 */
@RestController
@RequestMapping("/api/v1/recommendations/{userId}")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/frequent-routes")
    public ResponseEntity<List<FrequentRouteResponse>> getFrequentRoutes(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(recommendationService.getFrequentRoutes(userId, limit));
    }

    @GetMapping("/suggested-trains")
    public ResponseEntity<List<SuggestedTrainResponse>> getSuggestedTrains(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(recommendationService.getSuggestedTrains(userId, limit));
    }

    @GetMapping("/coach-recommendation")
    public ResponseEntity<List<CoachRecommendationResponse>> getCoachRecommendations(
            @PathVariable Long userId,
            @RequestParam Long scheduleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate journeyDate,
            @RequestParam(defaultValue = "1") int partySize,
            @RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(
                recommendationService.getCoachRecommendations(userId, scheduleId, journeyDate, partySize, limit));
    }

    @GetMapping("/saved-routes")
    public ResponseEntity<List<FavouriteRouteResponse>> getSavedRoutes(@PathVariable Long userId) {
        return ResponseEntity.ok(recommendationService.getSavedRoutes(userId));
    }
}
