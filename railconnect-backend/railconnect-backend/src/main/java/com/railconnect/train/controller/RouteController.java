package com.railconnect.train.controller;

import com.railconnect.journey.dtorequestresponse.RouteRequest;
import com.railconnect.journey.dtorequestresponse.RouteResponse;
import com.railconnect.train.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    // ==========================================
    //          🛡️ ADMIN MANAGEMENT ENDPOINTS
    // ==========================================

    @PostMapping("/admin/routes")
    public ResponseEntity<RouteResponse> createRoute(@Valid @RequestBody RouteRequest request) {
        RouteResponse createdRoute = routeService.createRoute(request);
        return new ResponseEntity<>(createdRoute, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/routes/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    //          🌐 PUBLIC INQUIRY ENDPOINTS
    // ==========================================

    @GetMapping("/routes")
    public ResponseEntity<List<RouteResponse>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/routes/{id}")
    public ResponseEntity<RouteResponse> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }
}