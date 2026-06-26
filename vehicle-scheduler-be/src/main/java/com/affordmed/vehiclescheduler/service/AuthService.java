package com.affordmed.vehiclescheduler.service;

import com.affordmed.vehiclescheduler.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

@Service
public class AuthService {

    private final WebClient webClient;

    @Value("${affordmed.company-name}")
    private String companyName;

    @Value("${affordmed.owner-name}")
    private String ownerName;

    @Value("${affordmed.roll-no}")
    private String rollNo;

    @Value("${affordmed.owner-email}")
    private String ownerEmail;

    @Value("${affordmed.access-code}")
    private String accessCode;

    // Cached credentials after registration
    private String clientID = "518afca5-768f-49fa-8e1d-2d6f395c1bc8";
private String clientSecret = "GDteeAqfeJUuyUKz";
    // Cached token
    private String accessToken;
    private long tokenExpiresAt = 0;

    public AuthService(WebClient affordmedWebClient) {
        this.webClient = affordmedWebClient;
    }

    /**
     * Register with the Affordmed test server to get clientID + clientSecret.
     * Call this once at startup or on demand.
     */
    public RegistrationResponse register() {
        RegistrationRequest req = new RegistrationRequest(
                companyName, ownerName, rollNo, ownerEmail, accessCode
        );

        RegistrationResponse response = webClient.post()
                .uri("/register")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(RegistrationResponse.class)
                .block();

        if (response != null) {
            this.clientID = response.getClientID();
            this.clientSecret = response.getClientSecret();
        }
        return response;
    }

    /**
     * Get a valid Bearer token, refreshing if expired.
     */
    public String getBearerToken() {
        // If token is still valid (with 60s buffer), return it
        if (accessToken != null && Instant.now().getEpochSecond() < tokenExpiresAt - 60) {
            return accessToken;
        }

        // Need to authenticate
        if (clientID == null || clientSecret == null) {
            throw new IllegalStateException(
                "Not registered yet. Call /register first or set clientID/clientSecret.");
        }

        AuthRequest req = new AuthRequest(
                companyName, clientID, clientSecret, ownerName, ownerEmail, rollNo
        );

        AuthResponse response = webClient.post()
                .uri("/auth")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(AuthResponse.class)
                .block();

        if (response != null) {
            this.accessToken = response.getAccessToken();
            this.tokenExpiresAt = Instant.now().getEpochSecond() + response.getExpiresIn();
        }

        return this.accessToken;
    }

    // Setters for manual credential injection (useful after registration)
    public void setClientCredentials(String clientID, String clientSecret) {
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.accessToken = null; // reset token so it's re-fetched
        this.tokenExpiresAt = 0;
    }
}
