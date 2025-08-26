package com.project.enquete.core.enquete_platform.security.services;

import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService service;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = service.findByEmail(email);

        if (user == null){
            throw new UsernameNotFoundException("Usuário não encontrado!");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(String.valueOf(user.getRole()))
                .build();
    }
}
