package com.railconnect.train.mapper;

import com.railconnect.entity.Coach;
import com.railconnect.journey.dtorequestresponse.CoachRequest;
import com.railconnect.journey.dtorequestresponse.CoachResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoachMapper {

    @Mapping(target = "train", ignore = true)
    @Mapping(target = "seats", ignore = true)
    Coach toEntity(CoachRequest request);

    @Mapping(target = "trainId", source = "train.id")
    @Mapping(target = "trainNumber", source = "train.number")
    @Mapping(target = "trainName", source = "train.name")
    @Mapping(target = "coachCode", source = "coachNumber")
    @Mapping(target = "totalSeats", source = "seatCount")
    CoachResponse toResponse(Coach coach);
}
