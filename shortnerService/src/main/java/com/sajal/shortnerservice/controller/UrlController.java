package com.sajal.shortnerservice.controller;

import com.sajal.shortnerservice.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlShortenerService urlService;

    @Autowired
    public UrlController(UrlShortenerService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/generate/{number}")
    public void generate(@PathVariable(name = "number") int number) {
        urlService.generateUrl(number);
    }

    @GetMapping("/{shortUrl}")
    public void update(@PathVariable String shortUrl) {
        urlService.update(shortUrl);
    }
}