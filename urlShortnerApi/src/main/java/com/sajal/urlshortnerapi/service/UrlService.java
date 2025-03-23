package com.sajal.urlshortnerapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sajal.urlshortnerapi.entity.UrlMapping;
import com.sajal.urlshortnerapi.repository.UrlRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UrlService {
    private final UrlRepository urlRepository;
    private final StringRedisTemplate redisTemplate;
    private final UrlFetchService urlFetchService;
    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);

    public UrlService(
            UrlRepository urlRepository,
            StringRedisTemplate redisTemplate,
            UrlFetchService urlFetchService) {
        this.urlRepository = urlRepository;
        this.redisTemplate = redisTemplate;
        this.urlFetchService = urlFetchService;
    }

    public String createShortUrl(@NotNull String longUrl) {
        logger.info("Creating short URL for long URL: {}", longUrl);
        String shortUrl = urlFetchService.fetchShortUrl();
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setLongUrl(longUrl);
        urlMapping.setClickCount(0);
        urlRepository.save(urlMapping);
        redisTemplate.opsForValue().set("url:" + shortUrl, longUrl , 1 , TimeUnit.DAYS);
        return shortUrl;
    }

    public String getLongUrl(@NotNull String shortUrl){
        String longUrl = redisTemplate.opsForValue().get("url:" + shortUrl);
        if (longUrl != null) {
            logger.info("Fetching long URL from redis. short URL: {}", shortUrl);
            return longUrl;
        }
        logger.info("Fetching long URL for short URL: {}", shortUrl);
        UrlMapping urlMapping = urlRepository.findById(shortUrl).orElseThrow(() -> new IllegalArgumentException("Invalid short URL"));
        return urlMapping.getLongUrl();
    }
}
