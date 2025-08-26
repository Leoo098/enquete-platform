package com.project.enquete.core.enquete_platform.security.services;

import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.security.auth.CustomAuthentication;
import com.project.enquete.core.enquete_platform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private final UserService userService;

    public User getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication instanceof CustomAuthentication customAuth){
            return customAuth.getUser();
        }

        return null;
    }
}
