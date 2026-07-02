package com.railconnect.pnr.dtorequestresponse;

public record PNRResponse(
        Long pnrId,
        String code,
        Long bookingId
) {
}