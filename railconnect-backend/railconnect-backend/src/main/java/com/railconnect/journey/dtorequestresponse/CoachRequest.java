package com.railconnect.journey.dtorequestresponse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CoachRequest {

    @NotNull(message = "Train ID is required")
    private Long trainId;

    @NotBlank(message = "Coach number is required (e.g., A1, B1)")
    private String coachNumber;

    @NotBlank(message = "Coach type is required (e.g., AC 2 Tier, Sleeper)")
    private String coachType;

    @NotNull(message = "Seat count configuration is required")
    @Min(value = 1, message = "Coach must contain at least 1 seat")
    private Integer seatCount;

    @NotNull(message = "Position sequence number is required")
    private Integer position;

    // --- EXPLICIT GETTERS FOR COMPILER SAFETY ---
    public Long getTrainId() { return trainId; }
    public String getCoachNumber() { return coachNumber; }
    public String getCoachType() { return coachType; }
    public Integer getSeatCount() { return seatCount; }
    public Integer getPosition() { return position; }
    
    public void setTrainId(Long trainId) { this.trainId = trainId; }
    public void setCoachNumber(String coachNumber) { this.coachNumber = coachNumber; }
    public void setCoachType(String coachType) { this.coachType = coachType; }
    public void setSeatCount(Integer seatCount) { this.seatCount = seatCount; }
    public void setPosition(Integer position) { this.position = position; }
}
