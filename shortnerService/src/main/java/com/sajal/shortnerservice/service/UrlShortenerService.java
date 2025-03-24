package com.sajal.shortnerservice.service;

import com.sajal.shortnerservice.entity.ShortUrl;
import com.sajal.shortnerservice.repository.ShortUrlRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

@Service
public class UrlShortenerService {
    private static final Logger logger = LoggerFactory.getLogger(UrlShortenerService.class);
    private final ShortUrlRepository shortUrlRepository;
    private final Tracer tracer;
    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public UrlShortenerService(ShortUrlRepository shortUrlRepository, Tracer tracer) {
        this.shortUrlRepository = shortUrlRepository;
        this.tracer = tracer;
    }

    private static String generateShortUrl() {
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(BASE62.charAt(RANDOM.nextInt(BASE62.length())));
        }
        return sb.toString();
    }

    @Transactional
    public void update(String shortUrl) {
        Span newSpan = tracer.nextSpan().name("updateShortUrl").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            logger.info("Updating short URL: {}", shortUrl);
            int result = shortUrlRepository.updateUsedByShortUrl(true, shortUrl);
            if (result == 0) {
                logger.error("Invalid short URL: {}", shortUrl);
                throw new IllegalArgumentException("Invalid short URL");
            }
            logger.info("Short URL updated successfully: {}", shortUrl);
        } finally {
            newSpan.end();
        }
    }

    @Transactional
    public boolean generateUrl(int number) {
        Span newSpan = tracer.nextSpan().name("generateUrls").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            logger.info("Generating {} short URLs", number);
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
        } finally {
            newSpan.end();
        }
    }

    @Transactional
    public String getUrl() {
        Span newSpan = tracer.nextSpan().name("getUrl").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            logger.info("Attempting to retrieve an unused short URL.");
            Optional<ShortUrl> shortUrl = shortUrlRepository.findFirstByUsedFalse();
            if (shortUrl.isPresent()) {
                logger.info("Found an unused short URL: {}", shortUrl.get().getShortUrl());
                update(shortUrl.get().getShortUrl());
                return shortUrl.get().getShortUrl();
            } else {
                logger.warn("No unused short URL found. Generating new short URLs.");
                boolean result = generateUrl(5);
                if (!result) {
                    logger.error("Error generating new short URLs.");
                    throw new IllegalArgumentException("Error getting short URL");
                }
                shortUrl = shortUrlRepository.findFirstByUsedFalse();
                String newShortUrl = shortUrl.orElseThrow().getShortUrl();
                logger.info("Generated and retrieved a new short URL: {}", newShortUrl);
                update(newShortUrl);
                return newShortUrl;
            }
        } finally {
            newSpan.end();
        }
    }
}