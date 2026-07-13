package com.railconnect.dashboard.service;

import com.railconnect.booking.dtorequestresponse.PassengerRequest;
import com.railconnect.common.enums.BookingStatus;
import com.railconnect.common.exception.InvalidRequestException;
import com.railconnect.common.exception.ResourceNotFoundException;
import com.railconnect.dashboard.dtorequestresponse.DashboardBookingResponse;
import com.railconnect.dashboard.dtorequestresponse.FavouriteRouteRequest;
import com.railconnect.dashboard.dtorequestresponse.FavouriteRouteResponse;
import com.railconnect.dashboard.mapper.DashboardMapper;
import com.railconnect.dashboard.repository.FavouriteRouteRepository;
import com.railconnect.entity.FavouriteRoute;
import com.railconnect.entity.Passenger;
import com.railconnect.entity.Route;
import com.railconnect.entity.User;
import com.railconnect.notification.dtorequestresponse.NotificationResponse;
import com.railconnect.notification.service.NotificationService;
import com.railconnect.reservation.repository.BookingRepository;
import com.railconnect.train.repository.RouteRepository;
import com.railconnect.user.dtorequestresponse.PassengerResponse;
import com.railconnect.user.dtorequestresponse.UpdateProfileRequest;
import com.railconnect.user.dtorequestresponse.UserResponse;
import com.railconnect.user.mapper.PassengerMapper;
import com.railconnect.user.mapper.UserMapper;
import com.railconnect.user.repository.PassengerRepository;
import com.railconnect.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;
    private final FavouriteRouteRepository favouriteRouteRepository;
    private final RouteRepository routeRepository;
    private final NotificationService notificationService;
    private final UserMapper userMapper;
    private final PassengerMapper passengerMapper;
    private final DashboardMapper dashboardMapper;

    public DashboardServiceImpl(UserRepository userRepository,
                                 PassengerRepository passengerRepository,
                                 BookingRepository bookingRepository,
                                 FavouriteRouteRepository favouriteRouteRepository,
                                 RouteRepository routeRepository,
                                 NotificationService notificationService,
                                 UserMapper userMapper,
                                 PassengerMapper passengerMapper,
                                 DashboardMapper dashboardMapper) {
        this.userRepository = userRepository;
        this.passengerRepository = passengerRepository;
        this.bookingRepository = bookingRepository;
        this.favouriteRouteRepository = favouriteRouteRepository;
        this.routeRepository = routeRepository;
        this.notificationService = notificationService;
        this.userMapper = userMapper;
        this.passengerMapper = passengerMapper;
        this.dashboardMapper = dashboardMapper;
    }

    // --- Profile ---

    @Override
    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        return userMapper.toResponse(findUser(userId));
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUser(userId);
        userMapper.updateUserFromRequest(request, user);
        return userMapper.toResponse(userRepository.save(user));
    }

    // --- Saved Passengers ---

    @Override
    @Transactional(readOnly = true)
    public List<PassengerResponse> getSavedPassengers(Long userId) {
        findUser(userId);
        return passengerRepository.findByUserId(userId).stream()
                .map(passengerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public PassengerResponse addSavedPassenger(Long userId, PassengerRequest request) {
        User user = findUser(userId);

        Passenger passenger = new Passenger();
        passenger.firstName = request.firstName();
        passenger.lastName = request.lastName();
        passenger.gender = request.gender();
        passenger.age = request.age();
        passenger.user = user;

        return passengerMapper.toResponse(passengerRepository.save(passenger));
    }

    @Override
    @Transactional
    public PassengerResponse updateSavedPassenger(Long userId, Long passengerId, PassengerRequest request) {
        Passenger passenger = findOwnedPassenger(userId, passengerId);
        passenger.firstName = request.firstName();
        passenger.lastName = request.lastName();
        passenger.gender = request.gender();
        passenger.age = request.age();
        return passengerMapper.toResponse(passengerRepository.save(passenger));
    }

    @Override
    @Transactional
    public void deleteSavedPassenger(Long userId, Long passengerId) {
        Passenger passenger = findOwnedPassenger(userId, passengerId);
        passengerRepository.delete(passenger);
    }

    // --- Trips ---

    @Override
    @Transactional(readOnly = true)
    public List<DashboardBookingResponse> getBookingHistory(Long userId) {
        findUser(userId);
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(dashboardMapper::toDashboardResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardBookingResponse> getUpcomingTrips(Long userId) {
        findUser(userId);
        List<BookingStatus> activeStatuses = List.of(
                BookingStatus.CONFIRMED, BookingStatus.RAC, BookingStatus.WAITING_LIST);
        return bookingRepository
                .findByUserIdAndJourneyDateGreaterThanEqualAndStatusInOrderByJourneyDateAsc(
                        userId, LocalDate.now(), activeStatuses)
                .stream()
                .map(dashboardMapper::toDashboardResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DashboardBookingResponse> getCancelledTrips(Long userId) {
        findUser(userId);
        return bookingRepository.findByUserIdAndStatusOrderByJourneyDateDesc(userId, BookingStatus.CANCELLED)
                .stream()
                .map(dashboardMapper::toDashboardResponse)
                .toList();
    }

    // --- Favourite Routes ---

    @Override
    @Transactional(readOnly = true)
    public List<FavouriteRouteResponse> getFavouriteRoutes(Long userId) {
        findUser(userId);
        return favouriteRouteRepository.findByUserId(userId).stream()
                .map(dashboardMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public FavouriteRouteResponse addFavouriteRoute(Long userId, FavouriteRouteRequest request) {
        User user = findUser(userId);
        Route route = routeRepository.findById(request.routeId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.routeId()));

        if (favouriteRouteRepository.existsByUserIdAndRouteId(userId, request.routeId())) {
            throw new InvalidRequestException("This route is already in your favourites.");
        }

        FavouriteRoute favouriteRoute = new FavouriteRoute();
        favouriteRoute.user = user;
        favouriteRoute.route = route;
        favouriteRoute.createdAt = LocalDateTime.now();

        return dashboardMapper.toResponse(favouriteRouteRepository.save(favouriteRoute));
    }

    @Override
    @Transactional
    public void removeFavouriteRoute(Long userId, Long favouriteRouteId) {
        FavouriteRoute favouriteRoute = favouriteRouteRepository.findByIdAndUserId(favouriteRouteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Favourite route", "id", favouriteRouteId));
        favouriteRouteRepository.delete(favouriteRoute);
    }

    // --- Notifications ---

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {
        findUser(userId);
        return notificationService.getHistory(userId);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private Passenger findOwnedPassenger(Long userId, Long passengerId) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger", "id", passengerId));
        if (passenger.user == null || !userId.equals(passenger.user.id)) {
            throw new ResourceNotFoundException("Passenger", "id", passengerId);
        }
        return passenger;
    }
}
