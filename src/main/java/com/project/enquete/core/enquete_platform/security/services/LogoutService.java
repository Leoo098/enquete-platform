package com.project.enquete.core.enquete_platform.security.services;

import com.project.enquete.core.enquete_platform.security.jwt.JwtTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final TokenBlacklistService tokenBlacklistService;
    private final JwtTokenService jwtTokenService;

    public void defaultLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        tokensHandle(request, response);
        response.sendRedirect("/");
    }

    public void forceLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        tokensHandle(request, response);
        response.sendRedirect("/login");
    }

    private void tokensHandle(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtTokenService.extractAccessToken(request);
        String refreshToken = jwtTokenService.extractRefreshToken(request);

        if (accessToken != null) tokenBlacklistService.addToBlacklist(accessToken);
        if (refreshToken != null) tokenBlacklistService.addToBlacklist(refreshToken);

        clearCookies(response);
    }

    public void clearCookies(HttpServletResponse response) {
        ResponseCookie clearAccessToken = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie clearRefreshToken = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie clearSessionCookie = ResponseCookie.from("JSESSIONID", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearAccessToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefreshToken.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearSessionCookie.toString());
    }

}
