package com.railconnect.train.mapper;

import com.railconnect.entity.Coach;
import com.railconnect.entity.Route;
import com.railconnect.entity.Schedule;
import com.railconnect.entity.Train;
import com.railconnect.train.dtorequestresponse.TrainDetailsResponse;
import com.railconnect.train.dtorequestresponse.TrainRequest;
import com.railconnect.train.dtorequestresponse.TrainResponse;
import com.railconnect.train.dtorequestresponse.TrainSearchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainMapper {

    // --- Core CRUD Mappings Required by TrainServiceImpl ---

    @Mapping(target = "coaches", ignore = true) // Ignore collections during flat generation
    Train toEntity(TrainRequest request);

    @Mapping(target = "coaches", source = "coaches") // Automatically maps using the default Coach mapper below
    TrainResponse toResponse(Train train);

    @Mapping(target = "id", ignore = true) // Ensure primary key doesn't get overwritten on updates
    @Mapping(target = "coaches", ignore = true)
    void updateEntityFromDto(TrainRequest request, @MappingTarget Train train);


    // --- Your Existing Search & Complex Metadata Mappings ---

    @Mapping(target = "trainId", source = "train.id")
    @Mapping(target = "routeId", source = "route.id")
    @Mapping(target = "scheduleId", source = "schedule.id")
    TrainSearchResponse toSearchResponse(Train train, Route route, Schedule schedule);

    @Mapping(target = "trainId", source = "train.id")
    @Mapping(target = "coachIds", source = "train.coaches")
    @Mapping(target = "scheduleIds", source = "route.schedules")
    TrainDetailsResponse toDetailsResponse(Train train, Route route);


    // --- Relationship Unwrapping Helpers ---

    default Long map(Coach coach) {
        return coach == null ? null : coach.getId();
    }

    default Long map(Schedule schedule) {
        return schedule == null ? null : schedule.getId();
    }
}