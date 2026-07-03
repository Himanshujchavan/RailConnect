package com.railconnect.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class JourneyValidator implements ConstraintValidator<ValidJourneyDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; 
        }
        LocalDate today = LocalDate.now();
        // Journey cannot be in the past
        return !value.isBefore(today);
    }
}