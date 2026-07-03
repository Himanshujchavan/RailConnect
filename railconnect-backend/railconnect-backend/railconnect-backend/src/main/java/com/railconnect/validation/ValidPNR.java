package com.railconnect.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PNRValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPNR {
    String message() default "Invalid PNR format. It must be exactly a 10-digit numeric string.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}