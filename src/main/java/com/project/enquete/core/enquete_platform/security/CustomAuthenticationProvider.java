package com.project.enquete.core.enquete_platform.security;

import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
       String login = authentication.getName();
       String typedPassword = authentication.getCredentials().toString();

        User userFound = userService.findByEmail(login);

        if(userFound == null){
            throw getUserNotFoundError();
        }

        String encryptedPassword = userFound.getPassword();

        boolean passwordsMatch = encoder.matches(typedPassword, encryptedPassword);

        if (passwordsMatch){
            return new CustomAuthentication(userFound);
        }

        throw getUserNotFoundError();
    }

    private UsernameNotFoundException getUserNotFoundError() {
        return new UsernameNotFoundException("Usuário e/ou senha incorretos!");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
