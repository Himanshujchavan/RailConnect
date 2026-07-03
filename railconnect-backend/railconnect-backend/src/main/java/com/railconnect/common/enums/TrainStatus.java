package com.railconnect.common.enums;

public enum TrainStatus {
    /**
     * Train is running as per its standard designated calendar schedule.
     */
    ACTIVE,

    /**
     * Train service has been formally cancelled for the designated day/route.
     */
    CANCELLED,

    /**
     * Train is operational but running behind its planned timetable.
     */
    DELAYED,

    /**
     * Train has been temporarily taken off service for routine mechanical maintenance.
     */
    MAINTENANCE
}