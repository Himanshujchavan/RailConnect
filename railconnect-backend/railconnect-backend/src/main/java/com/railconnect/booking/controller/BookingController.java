package com.railconnect.booking.controller;

import com.railconnect.booking.dtorequestresponse.BookingConfirmationResponse;
import com.railconnect.booking.dtorequestresponse.CancelBookingRequest;
import com.railconnect.booking.dtorequestresponse.CancellationResponse;
import com.railconnect.booking.dtorequestresponse.CreateBookingRequest;
import com.railconnect.reservation.service.BookingService;
import com.railconnect.reservation.service.CancellationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService bookingService;
    private final CancellationService cancellationService;

    public BookingController(BookingService bookingService, CancellationService cancellationService) {
        this.bookingService = bookingService;
        this.cancellationService = cancellationService;
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingConfirmationResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        return new ResponseEntity<>(bookingService.createBooking(request), HttpStatus.CREATED);
    }

    @PostMapping("/bookings/{id}/cancel")
    public ResponseEntity<CancellationResponse> cancelBooking(@PathVariable("id") Long bookingId,
                                                                @RequestBody(required = false) CancelBookingRequest request) {
        String reason = request != null ? request.reason() : null;
        return ResponseEntity.ok(cancellationService.cancelBooking(bookingId, reason));
    }
}