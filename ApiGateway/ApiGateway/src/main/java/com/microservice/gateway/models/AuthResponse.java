package com.microservice.gateway.models;

import lombok.*;

import java.util.Collection;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    // User ID (email from Okta)
    private String userId;

    // JWT access token - used for API authorization
    private String accessToken;

    // Refresh token - used to renew access token when expired
    private String refreshToken;

    // Token expiry timestamp (epoch seconds)
    private Long expireAt;

    // User's authorities/permissions (roles, scopes)
    private Collection<String> authorities;

}
