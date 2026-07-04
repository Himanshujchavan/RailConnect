package com.railconnect.journey.dtorequestresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoachResponse {
    private Long id;
    private Long trainId;
    private String trainNumber;
    private String trainName;
    private String coachCode;
    private String coachType;
    private Integer totalSeats;
}
