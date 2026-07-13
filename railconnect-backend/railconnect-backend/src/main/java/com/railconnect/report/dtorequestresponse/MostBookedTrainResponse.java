package com.railconnect.report.dtorequestresponse;

public record MostBookedTrainResponse(
        Long trainId,
        String trainNumber,
        String trainName,
        long bookingCount
) {
}
