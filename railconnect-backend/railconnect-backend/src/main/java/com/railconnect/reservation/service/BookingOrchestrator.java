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
import com.railconnect.entity.SeatAllocation;
import com.railconnect.entity.User;
import com.railconnect.entity.WaitingListAssignment;
import com.railconnect.reservation.repository.BookingPassengerRepository;
import com.railconnect.reservation.repository.BookingRepository;
import com.railconnect.reservation.repository.PNRRepository;
import com.railconnect.train.service.WaitingListService;
import com.railconnect.user.repository.PassengerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Phase 3 — Reservation Engine.
 * <p>
 * Coordinates the end-to-end reservation flow:
 * validate -> allocate seats -> calculate fare -> persist booking &amp; passengers -> generate PNR.
 * <p>
 * If the coach doesn't have enough free seats, the booking is no longer rejected outright —
 * it's still created (so the passenger has a PNR to track), and every passenger is pushed into
 * {@link WaitingListService}'s RAC/WL queue instead. The booking's final status (CONFIRMED,
 * RAC, or WAITING_LIST) reflects what actually happened.
 */
@Component
public class BookingOrchestrator {

    private final BookingValidator bookingValidator;
    private final SeatAllocationService seatAllocationService;
    private final FareService fareService;
    private final PNRService pnrService;
    private final WaitingListService waitingListService;
    private final BookingRepository bookingRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final PassengerRepository passengerRepository;
    private final PNRRepository pnrRepository;

    public BookingOrchestrator(BookingValidator bookingValidator,
                                SeatAllocationService seatAllocationService,
                                FareService fareService,
                                PNRService pnrService,
                                WaitingListService waitingListService,
                                BookingRepository bookingRepository,
                                BookingPassengerRepository bookingPassengerRepository,
                                PassengerRepository passengerRepository,
                                PNRRepository pnrRepository) {
        this.bookingValidator = bookingValidator;
        this.seatAllocationService = seatAllocationService;
        this.fareService = fareService;
        this.pnrService = pnrService;
        this.waitingListService = waitingListService;
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

        List<SeatAllocation> allocations = tryAllocateSeats(request);
        boolean seatsAllocated = !allocations.isEmpty();

        Booking savedBooking = saveBooking(request, user, coach, seatsAllocated);
        List<BookingPassenger> bookingPassengers = savePassengers(request.passengers(), user, savedBooking, allocations);

        if (!seatsAllocated) {
            assignToWaitingList(savedBooking, bookingPassengers);
        }

        String pnrCode = generateAndAttachPnr(savedBooking);

        List<Long> seatIds = allocations.stream()
                .map(a -> a.getSeat().getId())
                .toList();

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

    /**
     * Attempts seat allocation. If the coach simply doesn't have enough free seats right now
     * (a 409 from {@link SeatAllocationService}), that's treated as "route this booking through
     * RAC/Waiting List" rather than as a hard failure — any other error (bad coach/schedule id,
     * etc.) still propagates normally.
     */
    private List<SeatAllocation> tryAllocateSeats(CreateBookingRequest request) {
        boolean isFamilyBooking = isFamilyBooking(request.passengers());
        try {
            return isFamilyBooking
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
        } catch (ResponseStatusException ex) {
            if (HttpStatus.CONFLICT.equals(ex.getStatusCode())) {
                return List.of();
            }
            throw ex;
        }
    }

    private Booking saveBooking(CreateBookingRequest request, User user, Coach coach, boolean seatsAllocated) {
        Booking booking = new Booking();
        booking.createdAt = LocalDateTime.now();
        // Placeholder when seats weren't allocated — assignToWaitingList() sets the real
        // RAC vs WAITING_LIST status once we know which queue each passenger landed in.
        booking.status = seatsAllocated ? BookingStatus.CONFIRMED : BookingStatus.WAITING_LIST;
        booking.user = user;
        booking.scheduleId = request.scheduleId();
        booking.coachId = request.coachId();
        booking.journeyDate = request.journeyDate();
        booking.totalFare = fareService.calculateFare(coach.getCoachType(), request.passengers().size());
        return bookingRepository.save(booking);
    }

    private List<BookingPassenger> savePassengers(List<PassengerRequest> passengerRequests, User user, Booking savedBooking, List<SeatAllocation> allocations) {
        List<BookingPassenger> bookingPassengers = new ArrayList<>();

        for (int i = 0; i < passengerRequests.size(); i++) {
            PassengerRequest passengerRequest = passengerRequests.get(i);

            Passenger passenger = new Passenger();
            passenger.firstName = passengerRequest.firstName();
            passenger.lastName = passengerRequest.lastName();
            passenger.gender = passengerRequest.gender();
            passenger.user = user;
            Passenger savedPassenger = passengerRepository.save(passenger);

            BookingPassenger bookingPassenger = new BookingPassenger();
            bookingPassenger.booking = savedBooking;
            bookingPassenger.passenger = savedPassenger;

            // Only set when seats were actually allocated; waiting-list passengers get none.
            if (i < allocations.size()) {
                bookingPassenger.setSeatAllocation(allocations.get(i));
            }

            bookingPassengers.add(bookingPassengerRepository.save(bookingPassenger));
        }

        return bookingPassengers;
    }

    /**
     * Pushes every passenger on a seatless booking into the RAC/WL queue and sets the
     * booking's final status based on the worst queue any passenger landed in
     * (WAITING_LIST if any passenger got WL, otherwise RAC).
     */
    private void assignToWaitingList(Booking savedBooking, List<BookingPassenger> bookingPassengers) {
        boolean anyWaitingList = false;

        for (BookingPassenger bookingPassenger : bookingPassengers) {
            WaitingListAssignment assignment = waitingListService.addToQueue(
                    savedBooking.scheduleId,
                    savedBooking.journeyDate,
                    bookingPassenger.id,
                    0);

            if ("WL".equals(assignment.getQueueType())) {
                anyWaitingList = true;
            }
        }

        savedBooking.status = anyWaitingList ? BookingStatus.WAITING_LIST : BookingStatus.RAC;
        bookingRepository.save(savedBooking);
    }

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