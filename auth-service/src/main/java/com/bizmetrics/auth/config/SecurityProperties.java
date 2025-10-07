package com.bizmetrics.auth.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.security")
@Getter
@Setter
public class SecurityProperties {

    public enum Mode {
        NONE, BASIC, JWT
    }

    private Mode mode = Mode.NONE;

    // BASIC
    private String basicUsername = "admin";
    // USER {noop} senha ou um hash BCrypy

    private String basicPassword = "{noop}admin";

    // JWT
    private String jwtJwkSetUri; // Ex: Keycloak/Authrization Server

    private String jwtSecret;

    private String expectedAudience;

    // Rotas Abertas
    private List<String> permitAll = List.of(
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/**",
            "/auth/**"
    );

}
