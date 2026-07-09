package com.railconnect.reservation.service;

import com.railconnect.booking.dtorequestresponse.BookingConfirmationResponse;
import com.railconnect.booking.dtorequestresponse.CreateBookingRequest;
import com.railconnect.entity.Booking;
import com.railconnect.reservation.repository.BookingRepository;
import org.springframework.stereotype.Service;

/**
 * Thin facade only: request validation lives in {@link BookingValidator} and the actual
 * multi-step reservation flow (allocate seats -> fare -> persist -> PNR) lives in
 * {@link BookingOrchestrator}. This class exists purely so the rest of the app depends on
 * the {@link BookingService} interface rather than the orchestrator directly.
 * <p>
 * (This previously drifted into reimplementing the whole flow inline again, duplicating and
 * diverging from BookingOrchestrator, and along the way introduced a bug where the train's ID
 * was written into the booking's coachId column. Delegating here removes both problems.)
 */
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingOrchestrator bookingOrchestrator;
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingOrchestrator bookingOrchestrator, BookingRepository bookingRepository) {
        this.bookingOrchestrator = bookingOrchestrator;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingConfirmationResponse createBooking(CreateBookingRequest request) {
        return bookingOrchestrator.process(request);
    }

    @Override
    public Booking findByPnrCode(String pnrCode) {
        return bookingRepository.findByPnrCode(pnrCode).orElse(null);
    }
}
