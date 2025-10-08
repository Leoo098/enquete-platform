package com.project.enquete.core.enquete_platform.security.jwt;

import com.project.enquete.core.enquete_platform.security.services.LogoutService;
import com.project.enquete.core.enquete_platform.security.services.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenRefreshFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final LogoutService logoutService;
    private final TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Ignorar endpoints públicos
        if (path.startsWith("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("=== JWT CUSTOM FILTER ===");
        System.out.println("URL: " + request.getRequestURI());

        try {
            String accessToken = jwtTokenService.extractAccessToken(request);
            String refreshToken = jwtTokenService.extractRefreshToken(request);

            System.out.println("Access Token: " + (accessToken != null ? "PRESENT" : "NULL"));
            System.out.println("Refresh Token: " + (refreshToken != null ? "PRESENT" : "NULL"));

            if ((accessToken != null && blacklistService.isBlacklisted(accessToken)) ||
                    (refreshToken != null && blacklistService.isBlacklisted(refreshToken))){
                logoutService.forceLogout(request, response);
                return;
            }

            if (accessToken != null && jwtTokenService.isTokenExpired(accessToken)) {

                Map<String, String> newTokens = jwtTokenService.refreshTokens(refreshToken);

                String newAccessToken = newTokens.get("access_token");
                String newRefreshToken = newTokens.get("refresh_token");

                blacklistService.addToBlacklist(accessToken);
                blacklistService.addToBlacklist(refreshToken);

                ResponseCookie accessTokenCookie = jwtTokenService.createAccessTokenCookie(newAccessToken);
                ResponseCookie refreshTokenCookie = jwtTokenService.createRefreshTokenCookie(newRefreshToken);

                response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
                response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

                request = new CustomRequestWrapper(request, "Authorization", "Bearer " + newAccessToken);
            }

            filterChain.doFilter(request, response);

        }
        catch (HttpClientErrorException e) {
            logoutService.forceLogout(request, response);
        }
//        catch (OAuth2AuthorizationException e) {
//            if (e.getError().getErrorCode().equals("invalid_grant")) {
//                logoutService.forceLogout(request, response);
//            }
//        }
    }
}