package com.railconnect.journey.dtorequestresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    private Long id;
    private Long trainId;
    private String trainNumber;
    private String trainName;
    private Long routeId;
    private String routeName;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Set<String> operatingDays;
}