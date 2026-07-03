package com.railconnect.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TrainNumberValidator implements ConstraintValidator<ValidTrainNumber, String> {

    // Standard railway systems (like Indian Railways) use a 5-digit numbering system
    private static final String TRAIN_NUMBER_REGEX = "^\\d{5}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.matches(TRAIN_NUMBER_REGEX);
    }
}