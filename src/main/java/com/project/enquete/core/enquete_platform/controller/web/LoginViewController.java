package com.project.enquete.core.enquete_platform.controller.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.enquete.core.enquete_platform.model.Poll;
import com.project.enquete.core.enquete_platform.repository.PollRepository;
import com.project.enquete.core.enquete_platform.security.jwt.JwtTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class LoginViewController {

    private final JwtTokenService jwtTokenService;
    private final PollRepository pollRepository;

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/enquetes")
    public String enquetesPage(Model model, Authentication authentication){
        String username = authentication.getName();

        List<Poll> polls = pollRepository.findPollsByUsername(username);

        model.addAttribute("authentication", authentication);
        model.addAttribute("polls", polls);
        return "enquetes";
    }

    @GetMapping("/")
    public String homePage(Model model,Authentication authentication){
//    public String homePage(@AuthenticationPrincipal OAuth2User principal){
//        if (authentication instanceof CustomAuthentication auth){
//            System.out.println(auth);
//        }
        model.addAttribute("authentication", authentication);
        return "index";
    }

    @GetMapping("/authorized")
    public ResponseEntity<String> getAuthorizationCode(@RequestParam("code") String code,
                                                       HttpServletResponse response) throws JsonProcessingException {

        ResponseEntity<String> tokenResponse = jwtTokenService.exchangeCodeForToken(code);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(tokenResponse.getBody());
        String accessToken = jsonNode.get("access_token").asText();
        String refreshToken = jsonNode.get("refresh_token").asText();

        ResponseCookie accessTokenCookie = jwtTokenService.createAccessTokenCookie(accessToken);

        ResponseCookie refreshTokenCookie = jwtTokenService.createRefreshTokenCookie(refreshToken);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

//        return ResponseEntity.ok("Autenticado com sucesso!");

        // 4. Redireciona para check-cookies
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/"))
                .build();
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
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/"))
                .build();
    }

}
