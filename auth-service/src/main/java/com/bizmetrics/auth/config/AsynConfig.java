package com.bizmetrics.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import lombok.var;

@EnableAsync
@Configuration
public class AsynConfig {

    @Bean("asyncExecutor")
    public Executor asyncExecutor(AsyncProperties props, TaskDecorator taskDecorator){
        var executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("async-");
        executor.setCorePoolSize(props.getCorePoolSize());
        executor.setMaxPoolSize(props.getMaxPoolSize());
        executor.setQueueCapacity(props.getQueueCapacity());
        executor.setTaskDecorator(taskDecorator);
        executor.initialize();
        return executor;
    }

}
