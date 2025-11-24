package com.project.enquete.core.enquete_platform.security.jwt;

import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.security.auth.CustomAuthentication;
import com.project.enquete.core.enquete_platform.security.services.LogoutService;
import com.project.enquete.core.enquete_platform.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final JwtDecoder jwtDecoder;
    private final UserService userService;
    private final LogoutService logoutService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtTokenService.extractAccessToken(request);

        if (accessToken != null && !jwtTokenService.isTokenExpired(accessToken)) {
            try {
                Jwt jwt = jwtDecoder.decode(accessToken);
                String email = jwt.getClaim("email");

                User user = userService.findByEmail(email);

                if (user != null) {
                    CustomAuthentication auth = new CustomAuthentication(user);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                else {
                    logoutService.forceLogout(request, response);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}
