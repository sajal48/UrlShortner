package com.sajal.shortnerservice.controller;

import com.sajal.shortnerservice.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

class UrlControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UrlShortenerService urlService;


    @InjectMocks
    private UrlController urlController;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    void testGenerate() throws Exception {
        int number = 5;
        when(urlService.generateUrl(number)).thenReturn(true);

        mockMvc.perform(get("/api/urls/generate/{number}", number)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetUrl() throws Exception {
        String shortUrl = "abc123";
        when(urlService.getUrl()).thenReturn(shortUrl);

        mockMvc.perform(get("/api/urls/get")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(shortUrl));
    }
}