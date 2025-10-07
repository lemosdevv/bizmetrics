package com.bizmetrics.auth.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import org.springframework.cache.caffeine.CaffeineCacheManager;


@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(CacheProperties props) {
        var manager = new CaffeineCacheManager();
        manager.setCaffeineSpec(CaffeineSpec.parse(props.getSpec()));
        return manager;  
    }

}
