package com.project.enquete.core.enquete_platform.security;

import com.project.enquete.core.enquete_platform.controller.mappers.UserMapper;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.security.auth.CustomAuthentication;
import com.project.enquete.core.enquete_platform.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = (OAuth2AuthorizedClient) oauthToken.getPrincipal();

        if (client != null) {
            OAuth2AccessToken accessToken = client.getAccessToken();
            OAuth2RefreshToken refreshToken = client.getRefreshToken();

            ResponseCookie accessTokenCookie = ResponseCookie.from("ACCESS_TOKEN", accessToken.getTokenValue())
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ofSeconds(accessToken.getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond()))
                    .sameSite("Lax")
                    .build();

            response.addHeader("Set-Cookie", accessTokenCookie.toString());

            if (refreshToken != null){
                ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken.getTokenValue())
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(30 * 24 * 60 * 60)
                        .sameSite("Lax")
                        .build();

                response.addHeader("Set-Cookie", refreshTokenCookie.toString());
            }
        }

        User user = resolveAuthenticatedUser(authentication);

        authentication = new CustomAuthentication(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private User resolveAuthenticatedUser(Authentication authentication) {

        if (authentication instanceof OAuth2AuthenticationToken oauthToken){
            return handleOAuth2User(oauthToken.getPrincipal());
        }
        else if (authentication.getPrincipal() instanceof User user){
            return user;
        }
        return null;
    }

    private User handleOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        User user = userService.findByEmail(email);

        if (user == null){
            user = registerSocialUser(oAuth2User);
        }
        return user;
    }

    private User registerSocialUser(OAuth2User oAuth2User) {
        User newUser = new User();
        newUser.setEmail(oAuth2User.getAttribute("email"));
        newUser.setUsername(getLoginFromEmail(newUser.getEmail()));
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        userService.save(mapper.toDTO(newUser));

        return newUser;
    }

    private static String getLoginFromEmail(String email) {
        String emailFormatted = email.substring(0, email.indexOf("@"));

        if (emailFormatted.length() <= 19) {
            return emailFormatted;
        }

        return email.substring(0, 19);
    }

}
