package com.railconnect.common.util;

import com.railconnect.common.enums.BerthType;
import java.util.List;
import java.util.ArrayList;

public final class SeatAllocationUtil {

    private SeatAllocationUtil() {
        // Prevent instantiation
    }

    /**
     * Groups a flat array list of available seat structures by matching side preferences requested by the payload.
     */
    public static List<Long> filterSeatsByPreference(List<SeatInfo> availableSeats, BerthType preference, int count) {
        List<Long> selectedSeatIds = new ArrayList<>();

        // Phase 1: Try matching exact user seat placement preferences
        for (SeatInfo seat : availableSeats) {
            if (seat.getBerthType() == preference) {
                selectedSeatIds.add(seat.getSeatId());
                if (selectedSeatIds.size() == count) {
                    return selectedSeatIds;
                }
            }
        }

        // Phase 2: Fallback to taking the next available open sequence window if preference can't be met completely
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

    // Direct helper interface projection required for algorithm calculations
    public interface SeatInfo {
        Long getSeatId();
        BerthType getBerthType();
    }
}