package com.project.enquete.core.enquete_platform.config.security;

import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.security.auth.CustomAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.time.Duration;

@Configuration
public class TokenConfiguration {

    @Bean
    public TokenSettings tokenSettings(){
        return TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                .accessTokenTimeToLive(Duration.ofMinutes(60))
                .refreshTokenTimeToLive(Duration.ofMinutes(90))
                .build();
    }

    @Bean
    public ClientSettings clientSettings(){
        return ClientSettings.builder()
                .requireAuthorizationConsent(false)
//                .requireProofKey(true)
                .build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(){

        return context -> {
            Authentication principal = context.getPrincipal();

            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                if (principal instanceof CustomAuthentication customAuth) {
                    User user = customAuth.getUser();

                    context.getClaims()
                            .claim("username", user.getUsername())
                            .claim("email", user.getEmail())
                            .claim("authorities", customAuth.getAuthorities());
                }
            }
        };
    }
}
