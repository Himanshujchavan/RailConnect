package com.railconnect.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PNRValidator implements ConstraintValidator<ValidPNR, String> {

    private static final String PNR_REGEX = "^\\d{10}$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.matches(PNR_REGEX);
    }
}