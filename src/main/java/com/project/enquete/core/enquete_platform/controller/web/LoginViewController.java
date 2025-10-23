package com.project.enquete.core.enquete_platform.controller.web;

import com.project.enquete.core.enquete_platform.dto.request.UserDTO;
import com.project.enquete.core.enquete_platform.dto.response.OAuthTokenResponse;
import com.project.enquete.core.enquete_platform.form.UserForm;
import com.project.enquete.core.enquete_platform.security.jwt.JwtTokenService;
import com.project.enquete.core.enquete_platform.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LoginViewController {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;

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
        UserDTO dto = convertToDto(userForm);

        userService.validateUser(userForm, result);

        if (result.hasErrors()){
            return "register";
        }
        userService.save(dto);

        return "redirect:/";
    }

    private static UserDTO convertToDto(UserForm userForm) {
        return new UserDTO(
                userForm.getUsername(),
                userForm.getEmail(),
                userForm.getPassword(),
                userForm.getPasswordConfirmation()
        );
    }

    @GetMapping("/authorized")
    public String getAuthorizationCode(@RequestParam("code") String code,
                                                       HttpServletResponse response){

        OAuthTokenResponse tokenResponse = jwtTokenService.exchangeCodeForToken(code);

        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();

        ResponseCookie accessTokenCookie = jwtTokenService.createAccessTokenCookie(accessToken);

        ResponseCookie refreshTokenCookie = jwtTokenService.createRefreshTokenCookie(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

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
