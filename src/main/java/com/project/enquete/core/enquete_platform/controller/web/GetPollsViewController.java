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
@RequestMapping("/polls")
@RequiredArgsConstructor
public class GetPollsViewController {

    private final PollService pollService;

    @GetMapping
    public String pollsPage(Model model, Authentication authentication){
        String username = authentication.getName();

//        List<Poll> polls = pollRepository.findPollsByUsername(username);
        List<PollResponseDTO> polls = pollService.getAllPolls();

        model.addAttribute("authentication", authentication);
        model.addAttribute("polls", polls);
        return "polls";
    }
}
