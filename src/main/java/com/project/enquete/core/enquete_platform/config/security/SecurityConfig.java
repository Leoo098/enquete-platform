package com.project.enquete.core.enquete_platform.config.security;

import com.project.enquete.core.enquete_platform.security.CustomLogoutHandler;
import com.project.enquete.core.enquete_platform.security.UnifiedAuthenticationSuccessHandler;
import com.project.enquete.core.enquete_platform.security.jwt.JwtAuthenticationFilter;
import com.project.enquete.core.enquete_platform.security.jwt.TokenRefreshFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   UnifiedAuthenticationSuccessHandler successHandler,
                                                   CustomLogoutHandler customLogoutHandler,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   TokenRefreshFilter tokenRefreshFilter) throws Exception{
        return http
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/error", "/index", "/static/**", "/css/**", "/images/**").permitAll()
                        .requestMatchers("/login/**", "/register", "/auth/demo").permitAll()
                        .requestMatchers(HttpMethod.POST, "/clients").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login").permitAll()
                        .successHandler(successHandler)
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(successHandler))

                .oauth2ResourceServer(AbstractHttpConfigurer::disable)

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tokenRefreshFilter, BearerTokenAuthenticationFilter.class)

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(customLogoutHandler)
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID", "access_token", "refresh_token")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )
                .build();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults(){
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        var authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("");

        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return converter;
    }

}
