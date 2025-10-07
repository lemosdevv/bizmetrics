package com.bizmetrics.auth.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final SecurityProperties props;

    public SecurityConfig(SecurityProperties props) {
        this.props = props;
    }

    
@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Base settings
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .securityMatcher("/**")
                .authorizeHttpRequests(auth -> {
                    // Permitidos
                    props.getPermitAll().forEach(p -> auth.requestMatchers(p).permitAll());

                    // OPTIONS para CORS
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    // Demais
                    if (props.getMode() == SecurityProperties.Mode.NONE) {
                        auth.anyRequest().permitAll();
                    } else {
                        auth.anyRequest().authenticated();
                    }
                })
                .headers(h -> h.cacheControl(c -> {}))
                .requestCache(rc -> rc.disable())
                .anonymous(Customizer.withDefaults())
                .httpBasic(b -> {
                    if (props.getMode() == SecurityProperties.Mode.BASIC) {
                        // enabled
                    } else {
                        b.disable();
                    }
                });

        if (props.getMode() == SecurityProperties.Mode.JWT) {
            http.oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
            );
        }
            
        return http.build();
    }

     
    @Bean
        @ConditionalOnProperty(prefix = "app.security", name = "mode", havingValue = "BASIC")
        public UserDetailsService userDetailsService() {
            var user = User.withUsername(props.getBasicUsername())
                    .password(props.getBasicPassword()) // {noop}senha ou {bcrypt}hash
                    .roles("ADMIN")
                    .build();
            return new InMemoryUserDetailsManager(user);
    }

    // JWT decoder flexível: JWK set URI ou segredo HS256
    @Bean
    @ConditionalOnProperty(prefix = "app.security", name = "mode", havingValue = "JWT")
    public JwtDecoder jwtDecoder() {
        if (props.getJwtSecret() != null && !props.getJwtSecret().isBlank()) {
            SecretKey key = new SecretKeySpec(props.getJwtSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            return NimbusJwtDecoder.withSecretKey(key).build();
        } else if (props.getJwtJwkSetUri() != null && !props.getJwtJwkSetUri().isBlank()) {
            return NimbusJwtDecoder.withJwkSetUri(props.getJwtJwkSetUri()).build();
        }
        throw new IllegalStateException("Configure 'app.security.jwtSecret' ou 'app.security.jwtJwkSetUri' quando mode=JWT");
    }

    private org.springframework.core.convert.converter.Converter<org.springframework.security.oauth2.jwt.Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {
        var authoritiesMapper = new SimpleAuthorityMapper();
        authoritiesMapper.setConvertToUpperCase(true);
        authoritiesMapper.setPrefix("ROLE_"); // scopes -> ROLE_*

        var jwtGrantedAuthoritiesConverter = new org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("scope"); // ou "scp", "roles" conforme seu IdP

        return new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter() {{
            setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        }};
    }
}
