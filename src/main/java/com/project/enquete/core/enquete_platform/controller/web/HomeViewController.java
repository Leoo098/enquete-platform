package com.project.enquete.core.enquete_platform.controller.web;

import com.project.enquete.core.enquete_platform.dto.response.PollResponseDTO;
import com.project.enquete.core.enquete_platform.service.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeViewController {

    private final PollService pollService;

    @GetMapping
    public String indexPolls(Model model, Authentication authentication){
        List<PollResponseDTO> polls = pollService.getRandomPublicPolls();

        model.addAttribute("polls", polls);
        model.addAttribute("authentication", authentication);
        return "index";
    }

}
