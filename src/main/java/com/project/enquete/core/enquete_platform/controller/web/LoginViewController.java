package com.project.enquete.core.enquete_platform.controller.web;

import com.project.enquete.core.enquete_platform.controller.mappers.UserMapper;
import com.project.enquete.core.enquete_platform.dto.form.UserForm;
import com.project.enquete.core.enquete_platform.dto.request.UserDTO;
import com.project.enquete.core.enquete_platform.dto.validator.UserValidator;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.security.auth.CustomAuthentication;
import com.project.enquete.core.enquete_platform.security.jwt.JwtTokenService;
import com.project.enquete.core.enquete_platform.security.services.LogoutService;
import com.project.enquete.core.enquete_platform.service.DemoUserEnvironmentService;
import com.project.enquete.core.enquete_platform.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class LoginViewController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final JwtTokenService jwtTokenService;
    private final DemoUserEnvironmentService environmentService;
    private final LogoutService logoutService;
    private final UserMapper userMapper;

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user", new UserForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid UserForm userForm,
                               BindingResult result, HttpServletRequest request,
                               HttpServletResponse response){

        UserDTO dto = userService.convertToDto(userForm);

        userValidator.validateUser(dto, result);

        if (result.hasErrors()){
            return "register";
        }

        userService.save(dto);

        CustomAuthentication authentication = new CustomAuthentication(userMapper.toEntity(dto));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        User user = userService.findByEmail(dto.email().toLowerCase());
        if (user != null) {
            jwtTokenService.storeTokensInCookies(response, user);
        }

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        return "redirect:/";
    }

    @PostMapping("/auth/demo")
    public String createDemoUser(HttpServletRequest request,
                                 HttpServletResponse response){
        User user = environmentService.createTemporaryUser();

        CustomAuthentication authentication = new CustomAuthentication(user);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        jwtTokenService.storeTokensInCookies(response, user);

        environmentService.createSamplePoll();

        return "redirect:/";
    }

    @PostMapping("/delete")
    public void logoutAndDelete(Authentication authentication,
                                HttpServletRequest request,
                                HttpServletResponse response) throws IOException {
        User user = (User) authentication.getPrincipal();

        userService.delete(user.getId());

        logoutService.defaultLogout(request, response);
    }
}
