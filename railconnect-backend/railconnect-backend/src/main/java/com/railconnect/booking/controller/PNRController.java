package com.railconnect.booking.controller;

import com.railconnect.entity.Booking;
import com.railconnect.pnr.dtorequestresponse.PnrStatusResponse;
import com.railconnect.reservation.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pnr")
@RequiredArgsConstructor
public class PNRController {

    private final BookingService bookingService;

    @GetMapping("/{pnrCode}")
    public ResponseEntity<PnrStatusResponse> lookupPNRStatus(@PathVariable String pnrCode) {
        Booking booking = bookingService.findByPnrCode(pnrCode);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }

        List<PnrStatusResponse.PnrPassengerDto> passengerDtos = Collections.emptyList();
        if (booking.getBookingPassengers() != null) {
            passengerDtos = booking.getBookingPassengers().stream()
                    .map(bp -> new PnrStatusResponse.PnrPassengerDto(
                            bp.getId(),
                            bp.getPassenger() != null ? bp.getPassenger().getFirstName() + " " + bp.getPassenger().getLastName() : "Passenger",
                            bp.getPassenger() != null ? bp.getPassenger().getAge() : null,
                            bp.getPassenger() != null ? (bp.getPassenger().getGender() != null ? bp.getPassenger().getGender().name() : null) : null,
                            bp.getBerthPreference(),
                            bp.getSeatNumber(),
                            bp.getCurrentStatus()
                    ))
                    .toList();
        }

        String trainNumber = (booking.getSchedule() != null && booking.getSchedule().getTrain() != null)
                ? booking.getSchedule().getTrain().getNumber() : null;
        String trainName = (booking.getSchedule() != null && booking.getSchedule().getTrain() != null)
                ? booking.getSchedule().getTrain().getName() : null;
        String coachNumber = booking.getCoach() != null ? booking.getCoach().getCoachNumber() : null;
        String coachType = booking.getCoach() != null ? booking.getCoach().getCoachType() : null;

        PnrStatusResponse response = new PnrStatusResponse(
                booking.getPnrCode(),
                booking.getId(),
                booking.getStatus(),
                booking.getJourneyDate(),
                booking.getTotalFare(),
                trainNumber,
                trainName,
                coachNumber,
                coachType,
                booking.getCreatedAt(),
                passengerDtos
        );

        return ResponseEntity.ok(response);
    }
}
