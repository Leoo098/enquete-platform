package com.project.enquete.core.enquete_platform.security;

import com.project.enquete.core.enquete_platform.security.services.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final LogoutService logoutService;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        try {
            logoutService.defaultLogout(request, response);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
