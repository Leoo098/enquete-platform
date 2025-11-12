package com.project.enquete.core.enquete_platform.controller.web;

import com.project.enquete.core.enquete_platform.dto.request.UserDTO;
import com.project.enquete.core.enquete_platform.dto.validator.UserValidator;
import com.project.enquete.core.enquete_platform.dto.form.UserForm;
import com.project.enquete.core.enquete_platform.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LoginViewController {

    private final UserService userService;
    private final UserValidator userValidator;

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
                               BindingResult result){

        UserDTO dto = userService.convertToDto(userForm);

        userValidator.validateUser(dto, result);

        if (result.hasErrors()){
            return "register";
        }

        userService.save(dto);

        return "redirect:/";
    }

    @GetMapping("/check-cookies")
    public ResponseEntity<Map<String, String>> checkCookies(@CookieValue(name = "access_token", required = false) String accessToken,
                                                            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("access_token", accessToken != null ? "present" : "missing");
        cookies.put("refresh_token", refreshToken != null ? "present" : "missing");
        return ResponseEntity.ok(cookies);
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        return "redirect:/";
    }
}
