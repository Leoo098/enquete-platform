package com.project.enquete.core.enquete_platform.controller.web;

import com.project.enquete.core.enquete_platform.dto.response.PollResponseDTO;
import com.project.enquete.core.enquete_platform.service.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/polls")
@RequiredArgsConstructor
public class GetPollsViewController {

    private final PollService pollService;

    @GetMapping("/my-polls")
    public String pollsPage(Model model, Authentication authentication,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "5") int size,
                            @RequestParam(defaultValue = "createdAt") String sort
                            ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        Page<PollResponseDTO> pollsPage = pollService.getAllPolls(pageable);

        model.addAttribute("polls", pollsPage.getContent());
        model.addAttribute("authentication", authentication);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pollsPage.getTotalPages());
        model.addAttribute("totalItems", pollsPage.getTotalElements());
        model.addAttribute("pageSize", size);
        return "/polls/my-polls";
    }

    @GetMapping("/voted-polls")
    public String votedPollsPage(Model model, Authentication authentication,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 @RequestParam(defaultValue = "vote_date") String sort){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort));
        Page<PollResponseDTO> pollsPage = pollService.getVotedPolls(pageable);

        model.addAttribute("polls", pollsPage.getContent());
        model.addAttribute("authentication", authentication);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pollsPage.getTotalPages());
        model.addAttribute("totalItems", pollsPage.getTotalElements());
        model.addAttribute("pageSize", size);
        return "/polls/voted-polls";
    }
}
