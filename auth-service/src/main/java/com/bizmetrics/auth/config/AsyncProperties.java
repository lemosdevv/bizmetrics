package com.bizmetrics.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.async")
@Getter
@Setter
public class AsyncProperties {
    private int corePoolSize = 4;
    private int maxPoolSize = 16;
    private int queueCapacity = 200;
    private int schedulerPoolSize = 4;

}
