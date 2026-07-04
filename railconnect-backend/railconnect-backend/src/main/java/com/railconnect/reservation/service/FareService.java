package com.railconnect.reservation.service;

public interface FareService {
    double calculateFare(String coachType, int passengerCount);
}