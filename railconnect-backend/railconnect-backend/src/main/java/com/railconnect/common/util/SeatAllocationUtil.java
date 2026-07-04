package com.railconnect.common.util;

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
            String preference,
            int count) {

        List<Long> selectedSeatIds = new ArrayList<>();

        // Phase 1: Match preferred berth
        if (preference != null && !preference.isBlank()) {

            for (SeatInfo seat : availableSeats) {

                if (preference.equalsIgnoreCase(seat.getBerthType())) {

                    selectedSeatIds.add(seat.getSeatId());

                    if (selectedSeatIds.size() == count) {
                        return selectedSeatIds;
                    }
                }
            }
        }

        // Phase 2: Fill remaining seats
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
     * Find best seats.
     *
     * Strategy
     *
     * 1. Same coach
     * 2. Consecutive seats
     * 3. First available
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

            List<SeatInfo> window =
                    sortedSeats.subList(i, i + partySize);

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

        private final String berthType;

        public SeatInfo(
                Long seatId,
                Long coachId,
                String coachNumber,
                Integer seatNumber,
                String berthType) {

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

        public String getBerthType() {
            return berthType;
        }

        @Override
        public String toString() {
            return "SeatInfo{" +
                    "seatId=" + seatId +
                    ", coachNumber='" + coachNumber + '\'' +
                    ", seatNumber=" + seatNumber +
                    ", berthType='" + berthType + '\'' +
                    '}';
        }
    }
}