package com.railconnect.train.service;

import com.railconnect.journey.dtorequestresponse.ScheduleRequest;
import com.railconnect.journey.dtorequestresponse.ScheduleResponse;

import java.util.List;

public interface ScheduleService {
    ScheduleResponse createSchedule(ScheduleRequest request);
    List<ScheduleResponse> getAllSchedules();
    List<ScheduleResponse> getSchedulesByTrainId(Long trainId);
    void deleteSchedule(Long id);
}