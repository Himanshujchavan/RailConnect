package com.railconnect.train.dtorequestresponse;

import com.railconnect.common.enums.CoachType; // e.g., FIRST_AC, SECOND_AC, SLEEPER, AC_CHAIR_CAR

public class CoachResponse {
    private Long id;
    private Long trainId;
    private String coachNumber; // e.g., "A1", "B1", "S1"
    private CoachType coachType; // e.g., FIRST_AC, SECOND_AC, SLEEPER, AC_CHAIR_CAR
    private int totalSeats;

    public CoachResponse() {}

    public CoachResponse(Long id, Long trainId, String coachNumber, CoachType coachType, int totalSeats) {
        this.id = id;
        this.trainId = trainId;
        this.coachNumber = coachNumber;
        this.coachType = coachType;
        this.totalSeats = totalSeats;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTrainId() { return trainId; }
    public void setTrainId(Long trainId) { this.trainId = trainId; }

    public String getCoachNumber() { return coachNumber; }
    public void setCoachNumber(String coachNumber) { this.coachNumber = coachNumber; }

    public CoachType getCoachType() { return coachType; }
    public void setCoachType(CoachType coachType) { this.coachType = coachType; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
}