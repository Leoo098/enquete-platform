package com.project.enquete.core.enquete_platform.security.jwt;

import com.project.enquete.core.enquete_platform.security.services.LogoutService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenRefreshFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final LogoutService logoutService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtTokenService.extractAccessToken(request);

        if (accessToken != null && jwtTokenService.isTokenExpired(accessToken)){
            String refreshToken = jwtTokenService.extractRefreshToken(request);

            if (refreshToken == null || jwtTokenService.isTokenExpired(refreshToken)){
                logoutService.forceLogout(request, response);
                return;
            }

            String newAccessToken = String.valueOf(jwtTokenService.refreshAccessToken(refreshToken));

            ResponseCookie accessTokenCookie = jwtTokenService.createAccessTokenCookie(newAccessToken);

            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

            request = new CustomRequestWrapper(request, "Authorization", "Bearer " + newAccessToken);
        }

        filterChain.doFilter(request, response);
    }
}