package com.project.enquete.core.enquete_platform.security.jwt;

import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.security.auth.CustomAuthentication;
import com.project.enquete.core.enquete_platform.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtCustomAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (shouldConvert(authentication)){
            String login = authentication.getName();
            User user = userService.findByUsername(login);
            if (user != null){
                authentication = new CustomAuthentication(user);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }

    private boolean shouldConvert(Authentication authentication){
        return authentication instanceof JwtAuthenticationToken;
    }
}
