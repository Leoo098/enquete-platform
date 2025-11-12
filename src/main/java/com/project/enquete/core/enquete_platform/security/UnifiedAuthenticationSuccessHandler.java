package com.project.enquete.core.enquete_platform.security;

import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.repository.UserRepository;
import com.project.enquete.core.enquete_platform.security.auth.CustomAuthentication;
import com.project.enquete.core.enquete_platform.security.jwt.JwtTokenService;
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
public class UnifiedAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;
    private final PasswordEncoder encoder;
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {

        User user = null;

        if (authentication instanceof OAuth2AuthenticationToken oauthToken){
            OAuth2User oAuth2User = oauthToken.getPrincipal();
            String email = oAuth2User.getAttribute("email");
            user = getUserOrRegister(email);

            jwtTokenService.storeTokensInCookies(response, user);
        }
        else if (authentication instanceof CustomAuthentication) {
            User userPrincipal = (User) authentication.getPrincipal();
            user = userService.findByEmail(userPrincipal.getEmail());

            if (user != null){
                jwtTokenService.storeTokensInCookies(response, user);
            }
        }

        if (user != null){
            CustomAuthentication customAuth = new CustomAuthentication(user);
            SecurityContextHolder.getContext().setAuthentication(customAuth);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private User getUserOrRegister(String email) {
        User user = userService.findByEmail(email);
        if (user == null){
            user = registerUser(email);
        }
        return user;
    }

    private User registerUser(String email) {
        User user;
        user = new User();
        user.setUsername(getLoginFromEmail(email));
        user.setEmail(email);
        user.setPassword(encoder.encode(UUID.randomUUID().toString()));
        user.setSocialLogin(true);

        userRepository.save(user);
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

