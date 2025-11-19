package com.project.enquete.core.enquete_platform.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.enquete.core.enquete_platform.dto.response.OAuthTokenResponse;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.security.CustomRegisteredClientRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenService {

    private final JwtDecoder jwtDecoder;
    private final RestTemplate restTemplate;
    private final JwtEncoder jwtEncoder;
    private final CustomRegisteredClientRepository clientRepository;
    @Value("${app.base-url}")
    private String issuerUrl;

    public String generateAccessToken(User user) {
        try {
            Instant now = Instant.now();
            Instant expiry = now.plus(1, ChronoUnit.HOURS);

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer(issuerUrl)
                    .issuedAt(now)
                    .expiresAt(expiry)
                    .subject(user.getUsername())
                    .claim("email", user.getEmail())
                    .claim("roles", List.of(user.getRoles()))
                    .build();

            JwtEncoderParameters parameters = JwtEncoderParameters.from(claims);

            Jwt jwt = jwtEncoder.encode(parameters);

            return jwt.getTokenValue();

        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthenticationServiceException("Failed to generate token: " + e.getMessage());
        }
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(7, ChronoUnit.DAYS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuerUrl)
                .issuedAt(now)
                .expiresAt(expiry)
                .subject(user.getUsername())
                .claim("token_type", "refresh")
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(claims);
        Jwt jwt = jwtEncoder.encode(parameters);

        return jwt.getTokenValue();
    }

    public void storeTokensInCookies(HttpServletResponse response, User user){
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        ResponseCookie accessTokenCookie = createAccessTokenCookie(accessToken);
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

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
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", issuerUrl + "/authorized");

        HttpHeaders headers = createOAuthHeaders();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<OAuthTokenResponse> response = restTemplate.exchange(
                    issuerUrl + "/oauth2/token",
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
                issuerUrl + "/oauth2/token",
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

    private HttpHeaders createOAuthHeaders() {

        var client = clientRepository.findByClientId("enquete-client");

        if (client == null){
            throw new AuthenticationServiceException("Client enquete-client not found");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(client.getClientId(), client.getClientSecret());
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
