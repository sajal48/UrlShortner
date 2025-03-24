package com.sajal.urlshortnerapi.controller;

import com.sajal.urlshortnerapi.dto.ShortUrlRequestDto;
import com.sajal.urlshortnerapi.dto.ShortUrlResponseDto;
import com.sajal.urlshortnerapi.service.UrlService;
import com.sajal.urlshortnerapi.validator.ValidShortUrl;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
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
    private final Tracer tracer;

    public ShortenerController(UrlService urlService, Tracer tracer) {
        this.urlService = urlService;
        this.tracer = tracer;
    }

    @RequestMapping(path = "/url", method = POST, consumes = "application/json", produces = "application/json")
    ResponseEntity<ShortUrlResponseDto> shortenUrl(@Valid @RequestBody ShortUrlRequestDto requestDto) {
        Span newSpan = tracer.nextSpan().name("shortenUrl").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            String shortUrl = urlService.createShortUrl(requestDto.longUrl());
            ShortUrlResponseDto responseDto = new ShortUrlResponseDto(requestDto.longUrl(), shortUrl);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } finally {
            newSpan.end();
        }
    }

    @RequestMapping(path = "/url/{shortUrl}", method = GET)
    ResponseEntity<Void> getLongUrl(@PathVariable(name = "shortUrl") @ValidShortUrl String shortUrl) {
        Span newSpan = tracer.nextSpan().name("getLongUrl").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            String longUrl = urlService.getLongUrl(shortUrl);
            return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                    .location(URI.create(longUrl))
                    .build();
        } finally {
            newSpan.end();
        }
    }
}