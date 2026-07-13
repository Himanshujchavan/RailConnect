package com.railconnect.recommendation.dtorequestresponse;

public record CoachRecommendationResponse(
        Long coachId,
        String coachNumber,
        String coachType,
        int availableSeats,
        String reason
) {
}
