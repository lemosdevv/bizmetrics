package com.bizmetrics.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.cache")
@Getter
@Setter
public class CacheProperties {
    private String spec = "maximumSize=10000,expireAfterWrite=10m";
}
