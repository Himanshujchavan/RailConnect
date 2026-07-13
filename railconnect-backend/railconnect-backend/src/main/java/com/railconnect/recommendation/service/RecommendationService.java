package com.railconnect.recommendation.service;

import com.railconnect.dashboard.dtorequestresponse.FavouriteRouteResponse;
import com.railconnect.recommendation.dtorequestresponse.CoachRecommendationResponse;
import com.railconnect.recommendation.dtorequestresponse.FrequentRouteResponse;
import com.railconnect.recommendation.dtorequestresponse.SuggestedTrainResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Recommendation Engine. Everything here is inferred from a user's own booking history -
 * nothing is shared across users, so there's no cold-start "trending" fallback: a user with no
 * history simply gets empty suggestions rather than a guess.
 */
public interface RecommendationService {

    /**
     * The user's own routes, ranked by how often they've booked them (Frequently Travelled
     * Route).
     */
    List<FrequentRouteResponse> getFrequentRoutes(Long userId, int limit);

    /**
     * Trains running on the user's #1 frequent route (Suggested Trains). Empty if the user has
     * no booking history yet.
     */
    List<SuggestedTrainResponse> getSuggestedTrains(Long userId, int limit);

    /**
     * Recommends coaches on a specific schedule/date, preferring the coach type the user books
     * most often and otherwise favoring whichever coach currently has the most free seats
     * (Smart Coach Recommendation).
     */
    List<CoachRecommendationResponse> getCoachRecommendations(Long userId, Long scheduleId, LocalDate journeyDate,
                                                               int partySize, int limit);

    /**
     * Thin pass-through to Phase 7's Favourite Routes, so Saved Routes shows up under the same
     * Recommendation Engine surface without duplicating that data.
     */
    List<FavouriteRouteResponse> getSavedRoutes(Long userId);
}
