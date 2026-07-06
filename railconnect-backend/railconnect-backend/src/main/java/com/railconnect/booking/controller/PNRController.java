package com.railconnect.booking.controller;

import com.railconnect.entity.Booking;
import com.railconnect.reservation.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pnr")
@RequiredArgsConstructor
public class PNRController {

    private final BookingService bookingService;

    @GetMapping("/{pnrCode}")
    public ResponseEntity<Booking> lookupPNRStatus(@PathVariable String pnrCode) {
        Booking booking = bookingService.findByPnrCode(pnrCode);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(booking);
    }
}