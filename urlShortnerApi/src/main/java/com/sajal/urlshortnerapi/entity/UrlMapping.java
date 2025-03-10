package com.sajal.urlshortnerapi.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import lombok.Data;

@Data
@Table("url_mapping")
public class UrlMapping {
    
    @PrimaryKey
    private String shortUrl;
    private String longUrl;
    private int clickCount;

    public void incrementClickCount(int count) {
        this.clickCount += count;
    }
}
