package com.railconnect.train.dtorequestresponse;

import com.railconnect.common.enums.BerthType;

public class SeatAvailabilityResponse {
    private Long seatId;
    private String seatNumber; // e.g., "1", "2", "3"
    private BerthType seatType;
    private String coachNumber; // e.g., "A1"
    private boolean isAvailable;

    public SeatAvailabilityResponse() {}

    public SeatAvailabilityResponse(Long seatId, String seatNumber, BerthType seatType, String coachNumber, boolean isAvailable) {
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.coachNumber = coachNumber;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public BerthType getSeatType() { return seatType; }
    public void setSeatType(BerthType seatType) { this.seatType = seatType; }

    public String getCoachNumber() { return coachNumber; }
    public void setCoachNumber(String coachNumber) { this.coachNumber = coachNumber; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}
