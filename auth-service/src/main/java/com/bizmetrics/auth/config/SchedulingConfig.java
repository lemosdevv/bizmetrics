package com.bizmetrics.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableScheduling
@Configuration
public class SchedulingConfig {

    @Bean
    public TaskScheduler taskScheduler(AsyncProperties props){
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(props.getSchedulerPoolSize());
        scheduler.setThreadNamePrefix("scheduled-");
        scheduler.initialize();
        return scheduler;
    }
}
