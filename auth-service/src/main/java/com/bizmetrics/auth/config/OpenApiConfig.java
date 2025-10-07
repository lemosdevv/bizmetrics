package com.bizmetrics.auth.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenAPI(AppProperties app) {
        return new OpenAPI()
                .info(new Info()
                        .title("KPI Monitoring API")
                        .version(app.getVersion())
                        .description("API para cadastro de KPIs, metas, visualização e alertas (" + app.getEnvironment() + ")"))
                .components(new Components());
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("kpi")
                .pathsToMatch("/**")
                .build();
    }
}

