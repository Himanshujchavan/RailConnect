package com.railconnect.train.service;

import com.railconnect.journey.dtorequestresponse.CoachRequest;
import com.railconnect.journey.dtorequestresponse.CoachResponse;

import java.util.List;

public interface CoachService {
    CoachResponse addCoachToTrain(CoachRequest request);
    List<CoachResponse> getCoachesByTrain(Long trainId);
    void removeCoach(Long coachId);
}
