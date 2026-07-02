package com.railconnect.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = JourneyValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidJourneyDate {
    String message() default "Journey date must be today or in the future.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}