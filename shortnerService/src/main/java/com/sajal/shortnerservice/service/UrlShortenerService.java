package com.sajal.shortnerservice.service;

import com.sajal.shortnerservice.entity.ShortUrl;
import com.sajal.shortnerservice.repository.ShortUrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Service
public class UrlShortenerService {
    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);
    private final ShortUrlRepository shortUrlRepository;
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private static String generateShortUrl() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(BASE62.charAt(RANDOM.nextInt(BASE62.length())));
        }
        return sb.toString();
    }

    public UrlShortenerService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    @Transactional
    public void update(String shortUrl) {
        logger.info("Updating short URL: {}", shortUrl);
        int result = shortUrlRepository.updateUsedByShortUrl(true, shortUrl);
        if (result == 0) {
            logger.error("Invalid short URL: {}", shortUrl);
            throw new IllegalArgumentException("Invalid short URL");
        }
        logger.info("Short URL updated successfully: {}", shortUrl);
    }

    @Transactional
    public boolean generateUrl(int number) {
        logger.info("Generating {} short URLs", number);
        try {
            Set<String> shortUrls = new HashSet<>();
            IntStream.range(0, number).forEach(i -> {
                String shortUrl = generateShortUrl();
                shortUrls.add(shortUrl);
            });
            List<ShortUrl> generatedShortUrls = shortUrls.stream().map(shortUrl ->
                    ShortUrl.builder().shortUrl(shortUrl).used(false).build()).toList();
            shortUrlRepository.saveAll(generatedShortUrls);
            logger.info("Generated and saved {} short URLs successfully", number);
            return true;
        } catch (Exception e) {
            logger.error("Error generating short URLs", e);
            return false;
        }
    }
}