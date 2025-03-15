package com.sajal.shortnerservice.service;

import com.sajal.shortnerservice.entity.ShortUrl;
import com.sajal.shortnerservice.repository.ShortUrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Service
public class UrlShortenerService {
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
    public void update (String shortUrl){
       int result = shortUrlRepository.updateUsedByShortUrl(true, shortUrl);
         if (result == 0) {
              throw new IllegalArgumentException("Invalid short URL");
         }
         //updated
    }

    @Transactional
    public boolean generateUrl(int number) {
        try {
            Set<String> shortUrls = new HashSet<>();
            IntStream.range(0, number).forEach(i -> {
                String shortUrl = generateShortUrl();
                shortUrls.add(shortUrl);
            });
            List<ShortUrl> generatedShortUrls = shortUrls.stream().map(shortUrl ->
                    ShortUrl.builder().shortUrl(shortUrl).used(false).build()).toList();
            shortUrlRepository.saveAll(generatedShortUrls);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
