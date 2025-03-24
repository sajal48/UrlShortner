package com.sajal.urlshortnerapi.service;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
public class UrlFetchService {
    private final HttpClient httpClient;
    private final Tracer tracer;
    private final Propagator propagator;

    public UrlFetchService(Tracer tracer, Propagator propagator) {
        this.httpClient = HttpClient.newHttpClient();
        this.tracer = tracer;
        this.propagator = propagator;
    }

    public String fetchShortUrl() {
        Span newSpan = tracer.nextSpan().name("fetchShortUrl").start();
        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8082/api/urls/get"))
                    .GET();

            // Inject trace context into headers
            Map<String, String> headers = new HashMap<>();
            propagator.inject(newSpan.context(), headers, Map::put);
            headers.forEach(requestBuilder::header);

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch short URL", e);
        } finally {
            newSpan.end();
        }
    }
}