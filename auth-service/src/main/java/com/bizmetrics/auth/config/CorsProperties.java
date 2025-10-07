package com.bizmetrics.auth.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.cors")
@Getter
@Setter
public class CorsProperties {
    private List<String> allowedOrigins = List.of("http://localhost:3000", "http://localhost:8080");

    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");

    private List<String> allowedHeaders = List.of("Authorization", "Content-Type", "X-Requested-With", "X-Correlation-Id");

    private List<String> exposedHeaders = List.of("X-Correlation-Id");;
    
    private Boolean allowCredentials = true;

    private Long maxAge = 3600L;

}
