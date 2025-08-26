package com.project.enquete.core.enquete_platform.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtDecoder jwtDecoder;

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

    public boolean isTokenExpired(String accessToken){
        try {
            Jwt jwt = jwtDecoder.decode(accessToken);

            Instant expirationTime = jwt.getExpiresAt();
            return expirationTime != null && expirationTime.isBefore(Instant.now());
        } catch (JwtException e){
            return false;
        }
    }

    public ResponseEntity<String> exchangeCodeForToken(String code) {
        // Configura os parâmetros para a requisição do token
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", "http://localhost:8080/authorized");

        // Configura os headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("enquete-client", "admin4002"); // Basic auth

        // Faz a requisição POST para /oauth2/token
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            return restTemplate.postForEntity(
                    "http://localhost:8080/oauth2/token", // URL Authorization Server
                    request,
                    String.class
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .build();
    }

    public ResponseEntity<String> refreshAccessToken(String refreshToken){
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("enquete-client", "admin4002");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            return restTemplate.postForEntity(
                    "http://localhost:8080/oauth2/token",
                    request,
                    String.class
            );
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

}
