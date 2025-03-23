package com.sajal.urlshortnerapi.service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class UrlFetchService {
    private final HttpClient httpClient;

    public UrlFetchService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public String fetchShortUrl() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8082/api/urls/get"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch short URL", e);
        }
    }
}