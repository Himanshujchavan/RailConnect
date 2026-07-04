package com.railconnect.train.mapper;

import com.railconnect.entity.Schedule;
import com.railconnect.journey.dtorequestresponse.ScheduleRequest;
import com.railconnect.journey.dtorequestresponse.ScheduleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScheduleMapper {

    @Mapping(target = "train", ignore = true)
    @Mapping(target = "route", ignore = true)
    Schedule toEntity(ScheduleRequest request);

    @Mapping(target = "trainId", source = "train.id")
    @Mapping(target = "trainNumber", source = "train.number")
    @Mapping(target = "trainName", source = "train.name")
    @Mapping(target = "routeId", source = "route.id")
    @Mapping(target = "routeName", source = "route.routeName")
    ScheduleResponse toResponse(Schedule schedule);
}