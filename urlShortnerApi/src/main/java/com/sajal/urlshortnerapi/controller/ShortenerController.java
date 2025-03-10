package com.sajal.urlshortnerapi.controller;

import com.sajal.urlshortnerapi.dto.ShortUrlRequestDto;
import com.sajal.urlshortnerapi.dto.ShortUrlResponseDto;
import com.sajal.urlshortnerapi.service.UrlService;
import com.sajal.urlshortnerapi.validator.ValidShortUrl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api/v1")
public class ShortenerController {

    private final UrlService urlService;

    public ShortenerController(UrlService urlService) {
        this.urlService = urlService;
    }

    @RequestMapping(path = "/url", method = POST, consumes = "application/json", produces = "application/json")
    ResponseEntity<ShortUrlResponseDto> shortenUrl(@Valid @RequestBody ShortUrlRequestDto requestDto) {

        String shortUrl = urlService.createShortUrl(requestDto.longUrl());
        ShortUrlResponseDto responseDto = new ShortUrlResponseDto(requestDto.longUrl(), shortUrl);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @RequestMapping(path = "/url/{shortUrl}", method = GET)
    ResponseEntity<Void> getLongUrl(@PathVariable(name = "shortUrl") @ValidShortUrl String shortUrl) {
        String longUrl = urlService.getLongUrl(shortUrl);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .location(URI.create(longUrl))
                .build();
    }
}
