package com.railconnect.train.service;

import com.railconnect.train.dtorequestresponse.TrainRequest;
import com.railconnect.train.dtorequestresponse.TrainResponse;
import com.railconnect.common.enums.TrainType;
import com.railconnect.common.enums.TrainStatus;

import java.util.List;

public interface TrainService {
    TrainResponse createTrain(TrainRequest request);
    List<TrainResponse> getAllTrains();
    TrainResponse getTrainById(Long id);
    TrainResponse updateTrain(Long id, TrainRequest request);
    void deleteTrain(Long id);
    TrainResponse updateTrainStatus(Long id, TrainStatus status);
    List<TrainResponse> getTrainsByType(TrainType type);
}