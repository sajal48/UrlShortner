package com.sajal.shortnerservice.controller;

import com.sajal.shortnerservice.service.UrlShortenerService;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlShortenerService urlService;
    private final Tracer tracer;
    private final Propagator propagator;

    @Autowired
    public UrlController(UrlShortenerService urlService, Tracer tracer, Propagator propagator) {
        this.urlService = urlService;
        this.tracer = tracer;
        this.propagator = propagator;
    }

    @GetMapping("/generate/{number}")
    @ResponseStatus(HttpStatus.CREATED)
    public void generate(@PathVariable(name = "number") int number, @RequestHeader Map<String, String> headers) {
        Span newSpan = propagator.extract(headers, Map::get).start();
        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            urlService.generateUrl(number);
        } finally {
            newSpan.end();
        }
    }

    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public String getUrl(@RequestHeader Map<String, String> headers) {
        Span newSpan = propagator.extract(headers, Map::get).start();
        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            return urlService.getUrl();
        } finally {
            newSpan.end();
        }
    }
}