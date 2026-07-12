package com.railconnect.train.service;

import com.railconnect.journey.dtorequestresponse.RouteRequest;
import com.railconnect.journey.dtorequestresponse.RouteResponse;

import java.util.List;

public interface RouteService {
    RouteResponse createRoute(RouteRequest request);
    List<RouteResponse> getAllRoutes();
    RouteResponse getRouteById(Long id);
    RouteResponse updateRoute(Long id, RouteRequest request);
    void deleteRoute(Long id);
}