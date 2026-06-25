package com.microservice.gateway.controllers;

import com.microservice.gateway.models.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/google")
public class GoogleAuthController {

    @GetMapping("/login")
    public ResponseEntity<AuthResponse> login(

            @RegisteredOAuth2AuthorizedClient("google")
            OAuth2AuthorizedClient client,

            @AuthenticationPrincipal
            OAuth2User user) {

        AuthResponse response = new AuthResponse();

        response.setUserId(
                user.getAttribute("email")
        );

        response.setAccessToken(
                client.getAccessToken().getTokenValue()
        );

        if(client.getRefreshToken() != null){
            response.setRefreshToken(
                    client.getRefreshToken().getTokenValue()
            );
        }

        response.setExpireAt(
                client.getAccessToken()
                        .getExpiresAt()
                        .getEpochSecond()
        );

        response.setAuthorities(
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );

        return ResponseEntity.ok(response);
    }
}
