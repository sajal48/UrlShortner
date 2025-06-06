package com.sajal.urlshortnerapi.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ShortUrlValidator.class)
public  @interface ValidShortUrl {
    String message() default "Short URL must be exactly 6 characters long";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}