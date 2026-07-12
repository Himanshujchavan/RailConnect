package com.railconnect.train.service;

import com.railconnect.journey.dtorequestresponse.ScheduleRequest;
import com.railconnect.journey.dtorequestresponse.ScheduleResponse;

import java.util.List;

public interface ScheduleService {
    ScheduleResponse createSchedule(ScheduleRequest request);
    List<ScheduleResponse> getAllSchedules();
    ScheduleResponse getScheduleById(Long id);
    List<ScheduleResponse> getSchedulesByTrainId(Long trainId);
    ScheduleResponse updateSchedule(Long id, ScheduleRequest request);
    void deleteSchedule(Long id);
}