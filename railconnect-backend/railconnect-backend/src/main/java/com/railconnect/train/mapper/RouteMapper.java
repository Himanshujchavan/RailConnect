package com.railconnect.train.mapper;

import com.railconnect.entity.Route;
import com.railconnect.entity.RouteStation;
import com.railconnect.journey.dtorequestresponse.RouteRequest;
import com.railconnect.journey.dtorequestresponse.RouteResponse;
import com.railconnect.journey.dtorequestresponse.RouteStationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RouteMapper {

    @Mapping(target = "schedules", ignore = true)
    @Mapping(target = "routeStations", ignore = true)
    Route toEntity(RouteRequest request);

    @Mapping(target = "sourceStationId", source = "sourceStation.id")
    @Mapping(target = "sourceStationName", source = "sourceStation.stationName")
    @Mapping(target = "destinationStationId", source = "destinationStation.id")
    @Mapping(target = "destinationStationName", source = "destinationStation.stationName")
    RouteResponse toResponse(Route route);

    @Mapping(target = "stationId", source = "station.id")
    @Mapping(target = "stationCode", source = "station.stationCode")
    @Mapping(target = "stationName", source = "station.stationName")
    RouteStationResponse toResponse(RouteStation routeStation);
}