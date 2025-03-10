package com.sajal.urlshortnerapi.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ShortUrlValidator implements ConstraintValidator<ValidShortUrl, String> {
    @Override
    public void initialize(ValidShortUrl constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String shortUrl, ConstraintValidatorContext context) {
        return shortUrl != null && shortUrl.length() == 6;
    }
}