package com.railconnect.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StationValidator implements ConstraintValidator<ValidStationCode, String> {
    
    // Standard station codes are typically 3-6 uppercase letters (e.g., NDLS, HWH, BCT)
    private static final String STATION_CODE_REGEX = "^[A-Z0-9]{3,6}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Use @NotNull for null checks
        }
        return value.matches(STATION_CODE_REGEX);
    }
}