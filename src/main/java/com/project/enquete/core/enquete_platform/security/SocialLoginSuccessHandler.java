package com.project.enquete.core.enquete_platform.security;

import com.project.enquete.core.enquete_platform.controller.mappers.UserMapper;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.security.auth.CustomAuthentication;
import com.project.enquete.core.enquete_platform.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SocialLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        User user = userService.findByEmail(email);
        
        if (user == null){
            user = registerUser(email);
        }

        authentication = new CustomAuthentication(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private User registerUser(String email) {
        User user;
        user = new User();
        user.setUsername(getLoginFromEmail(email));
        user.setEmail(email);
        user.setPassword(encoder.encode(UUID.randomUUID().toString()));

        userService.save(mapper.toDTO(user));
        return user;
    }

    private static String getLoginFromEmail(String email) {
        String emailFormatted = email.substring(0, email.indexOf("@"));

        if (emailFormatted.length() <= 19) {
            return emailFormatted;
        }

        return email.substring(0, 19);
    }
}
