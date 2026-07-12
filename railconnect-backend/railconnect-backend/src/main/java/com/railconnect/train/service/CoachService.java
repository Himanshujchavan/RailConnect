package com.railconnect.train.service;

import com.railconnect.journey.dtorequestresponse.CoachRequest;
import com.railconnect.journey.dtorequestresponse.CoachResponse;

import java.util.List;

public interface CoachService {
    CoachResponse addCoachToTrain(CoachRequest request);
    CoachResponse updateCoach(Long coachId, CoachRequest request);
    List<CoachResponse> getCoachesByTrain(Long trainId);
    void removeCoach(Long coachId);

    /**
     * Wipes and rebuilds every seat on a coach from its current seatCount/coachType. Exposed as
     * its own admin action (rather than only firing implicitly on update) for cases like fixing
     * a bad seat layout without touching the coach's other fields.
     */
    CoachResponse regenerateSeats(Long coachId);
}
