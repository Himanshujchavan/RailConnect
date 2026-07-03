package com.railconnect.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StationValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStationCode {
    String message() default "Invalid station code. It must be 3 to 6 uppercase alphanumeric characters.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}