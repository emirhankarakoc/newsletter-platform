package com.karakoc.enewsletter.gmail.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karakoc.enewsletter.exceptions.general.BadRequestException;
import com.karakoc.enewsletter.gmail.googletoken.GoogleToken;
import com.karakoc.enewsletter.gmail.googletoken.GoogleTokenService;
import com.karakoc.enewsletter.user.User;
import com.karakoc.enewsletter.user.UserRepository;
import com.karakoc.enewsletter.user.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailAuthService {

    private final UserRepository userRepository;
    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    private final GoogleTokenService googleTokenService;

    public String buildAuthorizationUrl(String encodedToken) {
        return UriComponentsBuilder.fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid profile email https://www.googleapis.com/auth/gmail.send")
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .queryParam("state", encodedToken)
                .build().toUriString();
    }

    public void exchangeCodeAndSaveToken(String code, String userId) {
        try {
            String body = "code=" + code +
                    "&client_id=" + clientId +
                    "&client_secret=" + clientSecret +
                    "&redirect_uri=" + redirectUri +
                    "&grant_type=authorization_code";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://oauth2.googleapis.com/token"))
                    .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Token alma başarısız: {}", response.body());
                throw new RuntimeException("Failed to get token: " + response.body());
            }

            Map<String, Object> tokenResponse = new ObjectMapper().readValue(response.body(), Map.class);

            String idToken = (String) tokenResponse.get("id_token");
            if (idToken == null) {
                throw new BadRequestException("id_token bulunamadı, email alınamaz.");
            }
            DecodedJWT decodedJWT = JWT.decode(idToken);
            String connectedEmail = decodedJWT.getClaim("email").asString();
            String tokenId = UUID.randomUUID().toString();

            GoogleToken token = new GoogleToken();
            token.setId(tokenId);
            token.setOauth2EmailAddress(connectedEmail);
            token.setAccessToken((String) tokenResponse.get("access_token"));
            token.setRefreshToken((String) tokenResponse.get("refresh_token"));
            token.setExpiresAt(Instant.now().plusSeconds((Integer) tokenResponse.get("expires_in")));
            googleTokenService.save(token);

            User user = userRepository.findById(userId).orElseThrow();
            user.setGoogleTokenId(tokenId);
            user.setUserStatus(UserStatus.GOOGLE_VERIFICATED);
            userRepository.save(user);

            log.info("Gmail token başarıyla kaydedildi: {}", connectedEmail);
        } catch (Exception e) {
            log.error("Token exchange hatası", e);
            throw new RuntimeException("Token exchange failed", e);
        }
    }

    public String refreshAccessToken(GoogleToken token) {
        try {
            String body = "client_id=" + clientId +
                    "&client_secret=" + clientSecret +
                    "&refresh_token=" + token.getRefreshToken() +
                    "&grant_type=refresh_token";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://oauth2.googleapis.com/token"))
                    .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Access token yenileme başarısız: {}", response.body());
                throw new RuntimeException("Failed to refresh token: " + response.body());
            }

            Map<String, Object> tokenResponse = new ObjectMapper().readValue(response.body(), Map.class);
            String newAccessToken = (String) tokenResponse.get("access_token");
            Instant newExpiresAt = Instant.now().plusSeconds((Integer) tokenResponse.get("expires_in"));

            token.setAccessToken(newAccessToken);
            token.setExpiresAt(newExpiresAt);
            googleTokenService.save(token);

            log.info("Access token başarıyla yenilendi: {}", token.getOauth2EmailAddress());
            return newAccessToken;
        } catch (Exception e) {
            log.error("Access token refresh hatası", e);
            throw new RuntimeException("Access token refresh failed", e);
        }
    }
    public boolean isAccessTokenValid(String accessToken) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + accessToken))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                log.info("🔐 Token geçerli.");
                return true;
            } else {
                log.warn("⚠️ Token geçersiz. Status: {}, Body: {}", response.statusCode(), response.body());
                return false;
            }
        } catch (Exception e) {
            log.error("🛑 Token doğrulama hatası: {}", e.getMessage(), e);
            return false;
        }
    }

}
