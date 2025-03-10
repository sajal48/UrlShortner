package com.sajal.urlshortnerapi.dto;

import com.sajal.urlshortnerapi.validator.ValidLongUrl;

public record ShortUrlRequestDto(@ValidLongUrl String longUrl){}
