package com.sajal.urlshortnerapi.service;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShortUrlGeneratorTest {

    @Test
    public void testGenerateShortUrlLength() {
        String shortUrl = ShortUrlGenerator.generateShortUrl();
        assertEquals(6, shortUrl.length(), "Generated short URL should be 6 characters long");
    }
    @Test
    public void testGenerateShortUrlUniqueness() {
        Set<String> generatedUrls = new HashSet<>();
        int numberOfUrls = 1000;
        int retries = 0;
        int maxRetries = 10;

        for (int i = 0; i < numberOfUrls; i++) {
            String shortUrl;
            int attempt = 0;
            do {
                shortUrl = ShortUrlGenerator.generateShortUrl();
                attempt++;
            } while (!generatedUrls.add(shortUrl) && attempt < maxRetries);

            assertTrue(attempt < maxRetries, "Failed to generate a unique short URL after " + maxRetries + " attempts");
        }

        assertEquals(numberOfUrls, generatedUrls.size(), "All generated short URLs should be unique");
    }
}
