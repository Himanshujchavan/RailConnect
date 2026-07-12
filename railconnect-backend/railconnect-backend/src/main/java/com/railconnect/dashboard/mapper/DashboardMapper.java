package com.railconnect.dashboard.mapper;

import com.railconnect.dashboard.dtorequestresponse.DashboardBookingResponse;
import com.railconnect.dashboard.dtorequestresponse.FavouriteRouteResponse;
import com.railconnect.entity.Booking;
import com.railconnect.entity.FavouriteRoute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DashboardMapper {

    @Mapping(target = "bookingId", source = "id")
    @Mapping(target = "passengerCount",
            expression = "java(booking.bookingPassengers == null ? 0 : booking.bookingPassengers.size())")
    DashboardBookingResponse toDashboardResponse(Booking booking);

    @Mapping(target = "favouriteRouteId", source = "id")
    @Mapping(target = "routeId", source = "route.id")
    @Mapping(target = "routeName", source = "route.routeName")
    @Mapping(target = "sourceStationCode", source = "route.sourceStation.stationCode")
    @Mapping(target = "destinationStationCode", source = "route.destinationStation.stationCode")
    FavouriteRouteResponse toResponse(FavouriteRoute favouriteRoute);
}
