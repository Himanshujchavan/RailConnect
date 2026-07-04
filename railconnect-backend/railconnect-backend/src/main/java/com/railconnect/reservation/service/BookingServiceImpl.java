package com.railconnect.reservation.service;

import com.railconnect.booking.dtorequestresponse.BookingConfirmationResponse;
import com.railconnect.booking.dtorequestresponse.CreateBookingRequest;
import com.railconnect.entity.Booking;
import com.railconnect.entity.BookingPassenger;
import com.railconnect.entity.Coach;
import com.railconnect.entity.PNR;
import com.railconnect.entity.Passenger;
import com.railconnect.entity.User;
import com.railconnect.reservation.repository.BookingPassengerRepository;
import com.railconnect.reservation.repository.BookingRepository;
import com.railconnect.reservation.repository.PNRRepository;
import com.railconnect.train.repository.CoachRepository;
import com.railconnect.train.repository.TrainRepository;
import com.railconnect.user.repository.PassengerRepository;
import com.railconnect.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingPassengerRepository bookingPassengerRepository;
    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
        private final CoachRepository coachRepository;
    private final TrainRepository trainRepository;
    private final SeatAllocationService seatAllocationService;
    private final FareService fareService;
    private final PNRService pnrService;
    private final PNRRepository pnrRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              BookingPassengerRepository bookingPassengerRepository,
                              PassengerRepository passengerRepository,
                              UserRepository userRepository,
                              CoachRepository coachRepository,
                              TrainRepository trainRepository,
                              SeatAllocationService seatAllocationService,
                              FareService fareService,
                              PNRService pnrService,
                              PNRRepository pnrRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingPassengerRepository = bookingPassengerRepository;
        this.passengerRepository = passengerRepository;
        this.userRepository = userRepository;
        this.coachRepository = coachRepository;
        this.trainRepository = trainRepository;
        this.seatAllocationService = seatAllocationService;
        this.fareService = fareService;
        this.pnrService = pnrService;
        this.pnrRepository = pnrRepository;
    }

    @Override
    @Transactional
    public BookingConfirmationResponse createBooking(CreateBookingRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Coach coach = coachRepository.findById(request.coachId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coach not found"));

        trainRepository.findById(coach.getTrain().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Train not found"));

        List<Long> seatIds = seatAllocationService.allocateSeats(
                request.scheduleId(),
                request.coachId(),
                request.journeyDate(),
                request.seatPreference(),
                request.passengers().size()
        );

        Booking booking = new Booking();
        booking.createdAt = LocalDateTime.now();
        booking.status = com.railconnect.common.enums.BookingStatus.CONFIRMED;
        booking.user = user;
        booking.scheduleId = request.scheduleId();
        booking.coachId = request.coachId();
        booking.journeyDate = request.journeyDate();
        booking.totalFare = fareService.calculateFare(coach.getCoachType(), request.passengers().size());

        Booking savedBooking = bookingRepository.save(booking);

        List<BookingPassenger> bookingPassengers = request.passengers().stream()
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

        String pnrCode = pnrService.generatePNR();
        PNR pnr = new PNR();
        pnr.code = pnrCode;
        pnr.booking = savedBooking;
        pnrRepository.save(pnr);

        savedBooking.pnrCode = pnrCode;
        bookingRepository.save(savedBooking);

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
}