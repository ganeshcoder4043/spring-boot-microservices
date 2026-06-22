package com.microservice.gateway.controllers;

import com.microservice.gateway.models.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/login")
    public ResponseEntity<AuthResponse> login(
            // Inject the OAuth2 authorized client for "okta" provider
            // 'okta' must match the registration ID in application.yml
            @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient client,

            // ✅ Inject the authenticated OIDC user details
            // OidcUser contains user info from Okta (email, name, claims, etc.)
            @AuthenticationPrincipal OidcUser user,

            // Model is used for view rendering (not used here, but kept for future)
            Model model
    ) {
        // Log the user's email for debugging
        logger.info("user email id : {} ", user.getEmail());

        // Create response object
        AuthResponse authResponse = new AuthResponse();

        // Set user ID = email (from Okta's OIDC user info)
        authResponse.setUserId(user.getEmail());

        // Set access token from OAuth2 client (used for API authorization)
        authResponse.setAccessToken(client.getAccessToken().getTokenValue());

        // Set refresh token (used to get new access token when expired)
        authResponse.setRefreshToken(client.getRefreshToken().getTokenValue());

        // Set token expiry time in epoch seconds
        authResponse.setExpireAt(client.getAccessToken().getExpiresAt().getEpochSecond());

        // Extract authorities/permissions from the user
        // Authorities include: OIDC_USER, SCOPE_email, SCOPE_openid, etc.
        List<String> authorities = user.getAuthorities().stream().map(
                grantedAuthority -> {
                    return grantedAuthority.getAuthority();
                }).collect(Collectors.toList());

        // Set authorities in response
        authResponse.setAuthorities(authorities);

        // Return HTTP 200 OK with authResponse body
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

}


    // ✅ EXISTING — Okta Login
    /*@GetMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient client,
            @AuthenticationPrincipal OidcUser user) {

        logger.info("OKTA user email: {}", user.getEmail());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setUserId(user.getEmail());
        authResponse.setAccessToken(client.getAccessToken().getTokenValue());
        authResponse.setRefreshToken(client.getRefreshToken().getTokenValue());
        authResponse.setExpireAt(client.getAccessToken().getExpiresAt().getEpochSecond());

        List<String> authorities = user.getAuthorities()
                .stream()
                .map(ga -> ga.getAuthority())
                .collect(Collectors.toList());

        authResponse.setAuthorities(authorities);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    // ✅ NEW — Google Login
    @GetMapping("/google/login")
    public ResponseEntity<AuthResponse> googleLogin(
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient client,
            @AuthenticationPrincipal OAuth2User user) {

        // Google me OidcUser nahi, OAuth2User hota hai
//        logger.info("GOOGLE user email: {}", user.getAttribute("email"));

        AuthResponse authResponse = new AuthResponse();
        authResponse.setUserId(user.getAttribute("email"));
        authResponse.setAccessToken(client.getAccessToken().getTokenValue());

        // ⚠️ Google refresh token sirf pehli baar aata hai
        // getRefreshToken() null bhi ho sakta hai
        if (client.getRefreshToken() != null) {
            authResponse.setRefreshToken(client.getRefreshToken().getTokenValue());
        } else {
            authResponse.setRefreshToken("N/A - Google refresh token not provided");
        }

        authResponse.setExpireAt(client.getAccessToken().getExpiresAt().getEpochSecond());

        List<String> authorities = user.getAuthorities()
                .stream()
                .map(ga -> ga.getAuthority())
                .collect(Collectors.toList());

        authResponse.setAuthorities(authorities);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}
*/