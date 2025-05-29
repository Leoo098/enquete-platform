package com.project.enquete.core.enquete_platform.controller;

import com.project.enquete.core.enquete_platform.security.CustomAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginViewController {

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/")
    @ResponseBody
    public String homePage(Authentication authentication){
        if (authentication instanceof CustomAuthentication auth){
            System.out.println(auth);
        }
        return "Olá " + authentication.getName();
    }
}
