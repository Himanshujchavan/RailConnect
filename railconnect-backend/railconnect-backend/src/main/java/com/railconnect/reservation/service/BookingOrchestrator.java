package com.railconnect.reservation.service;

import com.railconnect.booking.dtorequestresponse.BookingConfirmationResponse;
import com.railconnect.booking.dtorequestresponse.CreateBookingRequest;
import com.railconnect.booking.dtorequestresponse.PassengerRequest;
import com.railconnect.common.enums.BookingStatus;
import com.railconnect.entity.Booking;
import com.railconnect.entity.BookingPassenger;
import com.railconnect.entity.Coach;
import com.railconnect.entity.PNR;
import com.railconnect.entity.Passenger;
import com.railconnect.entity.User;
import com.railconnect.reservation.repository.BookingPassengerRepository;
import com.railconnect.reservation.repository.BookingRepository;
import com.railconnect.reservation.repository.PNRRepository;
import com.railconnect.user.repository.PassengerRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Phase 4 — Reservation Engine.
 * <p>
 * Coordinates the end-to-end reservation flow:
 * validate -> allocate seats -> calculate fare -> persist booking &amp; passengers -> generate PNR.
 * This is the piece the roadmap calls out as "BookingOrchestrator" — it used to be inlined
 * directly inside {@code BookingServiceImpl.createBooking()}. Pulling it out here keeps
 * {@code BookingService}/{@code BookingServiceImpl} a thin facade, and keeps validation,
 * seat allocation, fare calculation and PNR generation each in their own single-purpose class.
 */
@Component
public class BookingOrchestrator {

    private final BookingValidator bookingValidator;
    private final SeatAllocationService seatAllocationService;
    private final FareService fareService;
    private final PNRService pnrService;
    private final BookingRepository bookingRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final PassengerRepository passengerRepository;
    private final PNRRepository pnrRepository;

    public BookingOrchestrator(BookingValidator bookingValidator,
                                SeatAllocationService seatAllocationService,
                                FareService fareService,
                                PNRService pnrService,
                                BookingRepository bookingRepository,
                                BookingPassengerRepository bookingPassengerRepository,
                                PassengerRepository passengerRepository,
                                PNRRepository pnrRepository) {
        this.bookingValidator = bookingValidator;
        this.seatAllocationService = seatAllocationService;
        this.fareService = fareService;
        this.pnrService = pnrService;
        this.bookingRepository = bookingRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.passengerRepository = passengerRepository;
        this.pnrRepository = pnrRepository;
    }

    @Transactional
    public BookingConfirmationResponse process(CreateBookingRequest request) {
        BookingValidator.ValidatedBooking validated = bookingValidator.validate(request);
        User user = validated.user();
        Coach coach = validated.coach();

        boolean isFamilyBooking = isFamilyBooking(request.passengers());
        List<Long> seatIds = isFamilyBooking
                ? seatAllocationService.allocateFamilySeats(
                        request.scheduleId(),
                        request.coachId(),
                        request.journeyDate(),
                        request.passengers().size())
                : seatAllocationService.allocateSeats(
                        request.scheduleId(),
                        request.coachId(),
                        request.journeyDate(),
                        request.seatPreference(),
                        request.passengers().size());

        Booking savedBooking = saveBooking(request, user, coach);
        List<BookingPassenger> bookingPassengers = savePassengers(request.passengers(), user, savedBooking);
        String pnrCode = generateAndAttachPnr(savedBooking);

        return new BookingConfirmationResponse(
                savedBooking.id,
                pnrCode,
                savedBooking.createdAt,
                savedBooking.status,
                user.id,
                savedBooking.scheduleId,
                savedBooking.coachId,
                savedBooking.journeyDate,
                savedBooking.totalFare,
                seatIds,
                bookingPassengers.stream().map(bp -> bp.id).toList()
        );
    }

    private Booking saveBooking(CreateBookingRequest request, User user, Coach coach) {
        Booking booking = new Booking();
        booking.createdAt = LocalDateTime.now();
        booking.status = BookingStatus.CONFIRMED;
        booking.user = user;
        booking.scheduleId = request.scheduleId();
        booking.coachId = request.coachId();
        booking.journeyDate = request.journeyDate();
        booking.totalFare = fareService.calculateFare(coach.getCoachType(), request.passengers().size());
        return bookingRepository.save(booking);
    }

    private List<BookingPassenger> savePassengers(List<PassengerRequest> passengerRequests, User user, Booking savedBooking) {
        return passengerRequests.stream()
                .map(passengerRequest -> {
                    Passenger passenger = new Passenger();
                    passenger.firstName = passengerRequest.firstName();
                    passenger.lastName = passengerRequest.lastName();
                    passenger.gender = passengerRequest.gender();
                    passenger.user = user;
                    Passenger savedPassenger = passengerRepository.save(passenger);

                    BookingPassenger bookingPassenger = new BookingPassenger();
                    bookingPassenger.booking = savedBooking;
                    bookingPassenger.passenger = savedPassenger;
                    return bookingPassengerRepository.save(bookingPassenger);
                })
                .collect(Collectors.toList());
    }

    /**
     * Auto-detects whether a booking should be treated as a family/group booking so
     * {@link #process} can route it through {@link SeatAllocationService#allocateFamilySeats}
     * instead of the standard preference-based {@link SeatAllocationService#allocateSeats}.
     * <p>
     * Rules:
     * 1. Passenger count is 3 or more, OR
     * 2. Two or more passengers share the exact same last name (case/whitespace-insensitive).
     */
    private boolean isFamilyBooking(List<PassengerRequest> passengers) {
        if (passengers == null || passengers.isEmpty()) {
            return false;
        }

        if (passengers.size() >= 3) {
            return true;
        }

        Set<String> uniqueLastNames = passengers.stream()
                .map(p -> p.lastName() != null ? p.lastName().trim().toLowerCase() : "")
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toSet());

        return uniqueLastNames.size() < passengers.size();
    }

    private String generateAndAttachPnr(Booking savedBooking) {
        String pnrCode = pnrService.generatePNR();
        PNR pnr = new PNR();
        pnr.code = pnrCode;
        pnr.booking = savedBooking;
        pnrRepository.save(pnr);

        savedBooking.pnrCode = pnrCode;
        bookingRepository.save(savedBooking);
        return pnrCode;
    }
}