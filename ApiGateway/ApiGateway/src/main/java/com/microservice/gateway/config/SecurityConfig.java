package com.microservice.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

   /*@Bean //-> also u use SecurityFilterChain, but they use in mvc & SecurityWebFilterChain for microservice
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity){
        httpSecurity
                // 1. Configure authorization rules
                .authorizeExchange(auth -> auth
                        // All requests need authentication (no public endpoints)
                        .anyExchange().authenticated()
                )
                // 2. Enable OAuth2 Client (for login flow with Okta/Auth0)
                // This handles the redirect to Okta login page
                .oauth2Client(oauth2 -> {})

                // 3. Enable OAuth2 Resource Server (for validating JWT tokens)
                // This allows the Gateway to validate tokens in Authorization headers
                .oauth2ResourceServer(oauth2 -> oauth2
                        // Configure JWT token validation
                        .jwt(jwt -> {}) // Empty lambda = use default JWT configuration
                );
        // Build the security filter chain and return it
        return httpSecurity.build();
    }*/


    //--------------------------------------


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(auth -> auth
                        .pathMatchers(
                                "/auth/okta/login",
                                "/auth/google/login",
                                "/oauth2/**",
                                "/login/**"
                        ).permitAll()
                        .anyExchange().authenticated()
                )

                .oauth2Login(oauth2 -> {})

                .oauth2Client(oauth2 -> {})

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {})
                );

        return http.build();
    }





    /*@Bean    // new way in 2026
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/public/**").permitAll()
                        .pathMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );
        return http.build();
    }*/
}
