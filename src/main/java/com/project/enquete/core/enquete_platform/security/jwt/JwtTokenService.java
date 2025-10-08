package com.project.enquete.core.enquete_platform.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.enquete.core.enquete_platform.dto.response.OAuthTokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenService {

    private final JwtDecoder jwtDecoder;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public String extractAccessToken(HttpServletRequest request){
        return extractToken(request, "access_token");
    }

    public String extractRefreshToken(HttpServletRequest request){
        return extractToken(request, "refresh_token");
    }

    public static String extractToken(HttpServletRequest request, String tokenName){

        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                if (tokenName.equals(cookie.getName())){
                    return cookie.getValue();
                }
            }
        }

        if ("access_token".equals(tokenName)) {
            String bearerToken = request.getHeader("Authorization");
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
        }

        return null;
    }

    public boolean isTokenExpired(String token){
        try {
            if (token == null) return true;
            Jwt jwt = jwtDecoder.decode(token);
            Instant expirationTime = jwt.getExpiresAt();
            return expirationTime != null && expirationTime.isBefore(Instant.now());
        } catch (JwtException e){
            return true;
        }
    }

    public OAuthTokenResponse exchangeCodeForToken(String code) {
        // Requisition parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", "http://localhost:8080/authorized");

        // Headers configuration
        HttpHeaders headers = createOAuthHeaders();

        // POST requisition to /oauth2/token
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<OAuthTokenResponse> response = restTemplate.exchange(
                    "http://localhost:8080/oauth2/token",
                    HttpMethod.POST,
                    request,
                    OAuthTokenResponse.class
            );

            validateTokenResponse(response.getBody());
            return response.getBody();

        }catch (HttpClientErrorException e){
            log.error("exchange code for token HTTP error: {}", e.getStatusCode());
            throw new AuthenticationServiceException("Authentication fail: " + e.getStatusCode());
        }
        catch (Exception e) {
            log.error("Unexpected error", e);
            throw new AuthenticationServiceException("Authentication intern error");
        }
    }

    public Map<String, String> refreshTokens(String refreshToken){
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);

        HttpHeaders headers = createOAuthHeaders();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<OAuthTokenResponse> response = restTemplate.exchange(
                "http://localhost:8080/oauth2/token",
                HttpMethod.POST,
                request,
                OAuthTokenResponse.class
        );

        System.out.println("Response: " + response.getBody());
        System.out.println("New Access Token: " + response.getBody().getAccessToken());
        System.out.println("New Refresh Token: " + response.getBody().getRefreshToken());

        validateTokenResponse(response.getBody());

        OAuthTokenResponse tokenResponse = response.getBody();
        return Map.of(
                "access_token", tokenResponse.getAccessToken(),
                "refresh_token", tokenResponse.getRefreshToken()
        );
    }

    private static HttpHeaders createOAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("enquete-client", "admin4002");
        return headers;
    }

    private void validateTokenResponse(OAuthTokenResponse tokenResponse) {
        if (tokenResponse == null){
            throw new AuthenticationServiceException("Response token null");
        }
        if (tokenResponse.getAccessToken() == null || tokenResponse.getAccessToken().isBlank()){
            throw new AuthenticationServiceException("Access token not received");
        }
    }

    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofMinutes(60))
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();
    }
}
