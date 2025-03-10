package com.sajal.urlshortnerapi.controller;

import com.sajal.urlshortnerapi.exceptionhandler.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShortenerController.class)
@Import(GlobalExceptionHandler.class)
public class ShortenerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testShortUrlReturn201() throws Exception{
        String longUrl = "http://www.abc.com";
        String requestBody = String.format("{\"longUrl\": \"%s\"}", longUrl);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/url")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.longUrl").value(longUrl));

    }
    @Test
    void testShortUrlReturn400() throws Exception{
        String longUrl = "htp:www.abc.com";
        String requestBody = String.format("{\"longUrl\": \"%s\"}", longUrl);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/url")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.longUrl").value("Invalid URL"));
    }
    @Test
    void testGetLongUrlReturns301() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/url/123456"))
                .andExpect(status().is(301))
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyString())));
    }

    @Test
    void testGetLongUrlReturns400() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/url/1234"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.shortUrl").value("Short URL must be exactly 6 characters long"));
    }
}
