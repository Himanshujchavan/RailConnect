package com.railconnect.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TrainNumberValidator.class) // Linked to its validator class below
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTrainNumber {
    String message() default "Invalid train number. It must be exactly 5 digits.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}