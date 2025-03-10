package com.sajal.urlshortnerapi.service;

import com.sajal.urlshortnerapi.entity.UrlMapping;
import com.sajal.urlshortnerapi.repository.UrlRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class UrlService {
    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String createShortUrl(@NotNull String longUrl) {
        String shortUrl = ShortUrlGenerator.generateShortUrl();
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setLongUrl(longUrl);
        urlMapping.setClickCount(0);
        urlRepository.save(urlMapping);
        return shortUrl;
    }

    public String getLongUrl(@NotNull String shortUrl){
        UrlMapping urlMapping = urlRepository.findById(shortUrl).orElseThrow(() -> new IllegalArgumentException("Invalid short URL"));
        return urlMapping.getLongUrl();
    }
}
