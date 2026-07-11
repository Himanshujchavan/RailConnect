package com.railconnect.search.dtorequestresponse;

public record CoachAvailabilityResponse(
        Long coachId,
        String coachNumber,
        String coachType,
        int totalSeats,
        int availableSeats
) {
}
