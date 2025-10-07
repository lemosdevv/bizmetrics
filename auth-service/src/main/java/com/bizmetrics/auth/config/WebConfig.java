package com.bizmetrics.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

@Configuration
public class WebConfig {

    private final CorsProperties cors;

    public WebConfig(CorsProperties cors) {
        this.cors = cors;
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(cors.getAllowedOrigins().toArray(String[]::new))
                        .allowedMethods(cors.getAllowedMethods().toArray(String[]::new))
                        .allowedHeaders(cors.getAllowedHeaders().toArray(String[]::new))
                        .exposedHeaders(cors.getExposedHeaders().toArray(String[]::new))
                        .allowCredentials(Boolean.TRUE.equals(cors.getAllowCredentials()))
                        .maxAge(cors.getMaxAge());
            }
        };
    }
    // Paginação padrão (ajuste conforme seu front)
    @Bean
    public PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver() {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setOneIndexedParameters(true); // page inicia em 1
        resolver.setFallbackPageable(PageRequest.of(0, 20)); // default page=1, size=20
        resolver.setMaxPageSize(200);
        return resolver;
    }

    // TaskDecorator para propagar MDC (correlation-id) em @Async
    @Bean
    public TaskDecorator mdcTaskDecorator() {
        return runnable -> {
            var contextMap = MDC.getCopyOfContextMap();
            return () -> {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                try {
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            };
        };
    }

    // Filtro: correlation-id em cada request
    @Bean
    public OncePerRequestFilter correlationIdFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(@NonNull HttpServletRequest request,
                                            @NonNull HttpServletResponse response,
                                            @NonNull FilterChain filterChain) throws ServletException, IOException {
                String headerName = "X-Correlation-Id";
                String correlationId = request.getHeader(headerName);
                if (!StringUtils.hasText(correlationId)) {
                    correlationId = UUID.randomUUID().toString();
                }
                MDC.put("correlationId", correlationId);
                response.setHeader(headerName, correlationId);
                response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
                try {
                    filterChain.doFilter(request, response);
                } finally {
                    MDC.remove("correlationId");
                }
            }
        };
    }
}
 
