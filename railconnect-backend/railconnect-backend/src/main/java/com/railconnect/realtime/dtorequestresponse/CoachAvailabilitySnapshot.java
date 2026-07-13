package com.railconnect.realtime.dtorequestresponse;

public record CoachAvailabilitySnapshot(
        Long coachId,
        String coachNumber,
        String coachType,
        int totalSeats,
        int availableSeats
) {
}
