package com.railconnect.common.util;

import com.railconnect.common.enums.BerthType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SeatAllocationUtil {

    private SeatAllocationUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Filter seats by berth preference.
     */
    public static List<Long> filterSeatsByPreference(
            List<SeatInfo> availableSeats,
            BerthType preference,
            int count) {

        List<Long> selectedSeatIds = new ArrayList<>();

        if (preference != null) {

            for (SeatInfo seat : availableSeats) {

                if (seat.getBerthType() == preference) {

                    selectedSeatIds.add(seat.getSeatId());

                    if (selectedSeatIds.size() == count) {
                        return selectedSeatIds;
                    }
                }
            }
        }

        // Fill remaining seats
        for (SeatInfo seat : availableSeats) {

            if (!selectedSeatIds.contains(seat.getSeatId())) {

                selectedSeatIds.add(seat.getSeatId());

                if (selectedSeatIds.size() == count) {
                    break;
                }
            }
        }

        return selectedSeatIds;
    }

    /**
     * Find best seats, preferring a consecutive block that contains at least one LOWER berth
     * when {@code preferLowerBerth} is set (used when a senior-citizen passenger is in the
     * party, per Smart Seat Allocation's lower-berth-priority rule). Falls back to the plain
     * consecutive-block search if no such window exists.
     */
    public static List<SeatInfo> computeOptimalAllocations(
            List<SeatInfo> availableSeats,
            int partySize,
            boolean preferLowerBerth) {

        if (!preferLowerBerth) {
            return computeOptimalAllocations(availableSeats, partySize);
        }

        if (availableSeats == null || availableSeats.isEmpty()) {
            return List.of();
        }

        List<SeatInfo> sortedSeats = availableSeats.stream()
                .sorted(
                        Comparator.comparing(SeatInfo::getCoachNumber)
                                .thenComparing(SeatInfo::getSeatNumber)
                )
                .toList();

        for (int i = 0; i <= sortedSeats.size() - partySize; i++) {
            List<SeatInfo> window = sortedSeats.subList(i, i + partySize);

            boolean sameCoach = window.stream().map(SeatInfo::getCoachNumber).distinct().count() == 1;
            boolean consecutive = true;
            for (int j = 1; j < window.size(); j++) {
                if (window.get(j).getSeatNumber() != window.get(j - 1).getSeatNumber() + 1) {
                    consecutive = false;
                    break;
                }
            }
            boolean hasLowerBerth = window.stream().anyMatch(seat -> seat.getBerthType() == BerthType.LOWER);

            if (sameCoach && consecutive && hasLowerBerth) {
                return new ArrayList<>(window);
            }
        }

        // No consecutive block happens to include a LOWER berth - fall back to the plain rule.
        return computeOptimalAllocations(availableSeats, partySize);
    }

    /**
     * Find best seats.
     */
    public static List<SeatInfo> computeOptimalAllocations(
            List<SeatInfo> availableSeats,
            int partySize) {

        if (availableSeats == null || availableSeats.isEmpty()) {
            return List.of();
        }

        List<SeatInfo> sortedSeats = availableSeats.stream()
                .sorted(
                        Comparator.comparing(SeatInfo::getCoachNumber)
                                .thenComparing(SeatInfo::getSeatNumber)
                )
                .toList();

        for (int i = 0; i <= sortedSeats.size() - partySize; i++) {

            List<SeatInfo> window = sortedSeats.subList(i, i + partySize);

            boolean sameCoach = window.stream()
                    .map(SeatInfo::getCoachNumber)
                    .distinct()
                    .count() == 1;

            boolean consecutive = true;

            for (int j = 1; j < window.size(); j++) {

                if (window.get(j).getSeatNumber()
                        != window.get(j - 1).getSeatNumber() + 1) {

                    consecutive = false;
                    break;
                }
            }

            if (sameCoach && consecutive) {
                return new ArrayList<>(window);
            }
        }

        return sortedSeats.stream()
                .limit(partySize)
                .toList();
    }

    /**
     * Seat projection used by allocation algorithm.
     */
    public static class SeatInfo {

        private final Long seatId;
        private final Long coachId;
        private final String coachNumber;
        private final Integer seatNumber;
        private final BerthType berthType;

        public SeatInfo(
                Long seatId,
                Long coachId,
                String coachNumber,
                Integer seatNumber,
                BerthType berthType) {

            this.seatId = seatId;
            this.coachId = coachId;
            this.coachNumber = coachNumber;
            this.seatNumber = seatNumber;
            this.berthType = berthType;
        }

        public Long getSeatId() {
            return seatId;
        }

        public Long getCoachId() {
            return coachId;
        }

        public String getCoachNumber() {
            return coachNumber;
        }

        public Integer getSeatNumber() {
            return seatNumber;
        }

        public BerthType getBerthType() {
            return berthType;
        }

        @Override
        public String toString() {
            return "SeatInfo{" +
                    "seatId=" + seatId +
                    ", coachNumber='" + coachNumber + '\'' +
                    ", seatNumber=" + seatNumber +
                    ", berthType=" + berthType +
                    '}';
        }
    }
}