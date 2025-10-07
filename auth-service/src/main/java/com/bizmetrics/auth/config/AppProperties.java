package com.bizmetrics.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
    private String name = "Auth Service";
    private String version = "1.0.0";
    private String environment = "local";
}
