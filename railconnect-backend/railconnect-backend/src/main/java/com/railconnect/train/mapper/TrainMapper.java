package com.railconnect.train.mapper;

import com.railconnect.entity.Coach;
import com.railconnect.entity.Route;
import com.railconnect.entity.Schedule;
import com.railconnect.entity.Train;
import com.railconnect.train.dtorequestresponse.TrainDetailsResponse;
import com.railconnect.train.dtorequestresponse.TrainSearchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainMapper {

    @Mapping(target = "trainId", source = "train.id")
    @Mapping(target = "routeId", source = "route.id")
    @Mapping(target = "scheduleId", source = "schedule.id")
    TrainSearchResponse toSearchResponse(Train train, Route route, Schedule schedule);

    @Mapping(target = "trainId", source = "train.id")
    @Mapping(target = "coachIds", source = "train.coaches")
    @Mapping(target = "scheduleIds", source = "route.schedules")
    TrainDetailsResponse toDetailsResponse(Train train, Route route);

    default Long map(Coach coach) {
        return coach == null ? null : coach.id;
    }

    default Long map(Schedule schedule) {
        return schedule == null ? null : schedule.id;
    }
}