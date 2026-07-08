package com.railconnect.booking.dtorequestresponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BookingReviewRequest {

    @NotNull(message = "Schedule ID is required")
    private Long scheduleId;

    @NotNull(message = "Source station ID is required")
    private Long sourceStationId;

    @NotNull(message = "Destination station ID is required")
    private Long destinationStationId;

    @NotNull(message = "Coach class is required")
    private String coachClass; // e.g., "FIRST_AC", "SLEEPER"

    @NotEmpty(message = "Passenger list cannot be empty")
    @Valid // Validates every passenger object inside the list
    private List<PassengerRequest> passengers;

    // Getters and Setters
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }

    public Long getSourceStationId() { return sourceStationId; }
    public void setSourceStationId(Long sourceStationId) { this.sourceStationId = sourceStationId; }

    public Long getDestinationStationId() { return destinationStationId; }
    public void setDestinationStationId(Long destinationStationId) { this.destinationStationId = destinationStationId; }

    public String getCoachClass() { return coachClass; }
    public void setCoachClass(String coachClass) { this.coachClass = coachClass; }

    public List<PassengerRequest> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerRequest> passengers) { this.passengers = passengers; }

    /**
     * Inner helper DTO specifically for capturing passenger details during the review phase.
     */
    public static class PassengerRequest {
        
        @NotNull(message = "Passenger name is required")
        private String name;

        @NotNull(message = "Passenger age is required")
        private Integer age;

        @NotNull(message = "Passenger gender is required")
        private String gender;

        private String preferredBerth; // Optional preference (e.g., "LOWER", "UPPER")
        private String quotaType; // Quota type (GENERAL, LADIES, SENIOR_CITIZEN)

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }

        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }

        public String getPreferredBerth() { return preferredBerth; }
        public void setPreferredBerth(String preferredBerth) { this.preferredBerth = preferredBerth; }
        public String getQuotaType() { return quotaType; }
        public void setQuotaType(String quotaType) { this.quotaType = quotaType; }
    }
}