package com.bizmetrics.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.context.MessageSource;

@Configuration
public class ValidationConfig {

    @Bean
    public MessageSource MessageSource(){
        var ms = new ReloadableResourceBundleMessageSource();
        ms.setBasename("classpath:messages");
        ms.setDefaultEncoding("UTF-8");
        ms.setUseCodeAsDefaultMessage(true);
        return ms;
    }

    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource){
        var factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource);
        return factory;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(LocalValidatorFactoryBean validator){
        var processor = new MethodValidationPostProcessor();
        processor.setValidator(validator);
        return processor;
    }
}
