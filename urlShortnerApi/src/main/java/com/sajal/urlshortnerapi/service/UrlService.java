package com.sajal.urlshortnerapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sajal.urlshortnerapi.entity.UrlMapping;
import com.sajal.urlshortnerapi.repository.UrlRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;

import java.util.concurrent.TimeUnit;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final StringRedisTemplate redisTemplate;
    private final UrlFetchService urlFetchService;
    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);
    private final Tracer tracer;

    public UrlService(
            UrlRepository urlRepository,
            StringRedisTemplate redisTemplate,
            UrlFetchService urlFetchService,
            Tracer tracer) {
        this.urlRepository = urlRepository;
        this.redisTemplate = redisTemplate;
        this.urlFetchService = urlFetchService;
        this.tracer = tracer;
    }

    public String createShortUrl(@NotNull String longUrl) {
        Span newSpan = tracer.nextSpan().name("createShortUrl").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            logger.info("Creating short URL for long URL: {}", longUrl);
            String shortUrl = urlFetchService.fetchShortUrl();
            UrlMapping urlMapping = new UrlMapping();
            urlMapping.setShortUrl(shortUrl);
            urlMapping.setLongUrl(longUrl);
            urlMapping.setClickCount(0);
            urlRepository.save(urlMapping);
            redisTemplate.opsForValue().set("url:" + shortUrl, longUrl , 1 , TimeUnit.DAYS);
            return shortUrl;
        } finally {
            newSpan.end();
        }
    }

    public String getLongUrl(@NotNull String shortUrl){
        Span newSpan = tracer.nextSpan().name("getLongUrl").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            String longUrl = redisTemplate.opsForValue().get("url:" + shortUrl);
            if (longUrl != null) {
                logger.info("Fetching long URL from redis. short URL: {}", shortUrl);
                return longUrl;
            }
            logger.info("Fetching long URL for short URL: {}", shortUrl);
            UrlMapping urlMapping = urlRepository.findById(shortUrl).orElseThrow(() -> new IllegalArgumentException("Invalid short URL"));
            return urlMapping.getLongUrl();
        } finally {
            newSpan.end();
        }
    }
}