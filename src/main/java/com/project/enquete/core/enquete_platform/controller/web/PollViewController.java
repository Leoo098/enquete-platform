package com.project.enquete.core.enquete_platform.controller.web;

import com.project.enquete.core.enquete_platform.controller.mappers.VoteMapper;
import com.project.enquete.core.enquete_platform.dto.request.OptionDTO;
import com.project.enquete.core.enquete_platform.dto.request.PollDTO;
import com.project.enquete.core.enquete_platform.dto.request.VoteDTO;
import com.project.enquete.core.enquete_platform.dto.response.PollResponseDTO;
import com.project.enquete.core.enquete_platform.form.PollForm;
import com.project.enquete.core.enquete_platform.model.Poll;
import com.project.enquete.core.enquete_platform.model.TimeUnit;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.service.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/poll")
@RequiredArgsConstructor
public class PollViewController {

    private final PollService pollService;
    private final VoteMapper voteMapper;

    @GetMapping("/create")
    public String showCreateForm(Model model, Authentication authentication) {
        model.addAttribute("authentication", authentication);
        model.addAttribute("poll", new PollForm());
        model.addAttribute("timeUnits", getTimeUnits());
        return "poll/create";
    }

    @PostMapping("/create")
    public String createPoll(@ModelAttribute @Valid PollForm pollForm,
                             BindingResult result, Model model){

        if (result.hasErrors()){
            model.addAttribute("timeUnits", getTimeUnits());
            return "poll/create";
        }

        PollDTO dto = convertToDto(pollForm);

        try {
            pollService.createPoll(dto);
            return "redirect:/polls/my-polls";
        }
        catch (Exception e){
            model.addAttribute("error", "Erro ao criar enquete");
            model.addAttribute("timeUnits", getTimeUnits());
            return "poll/create";
        }
    }

    @PostMapping("/{id}")
    public String votePoll(@RequestParam(value = "option") Long optionId){
        VoteDTO voteDTO = new VoteDTO(optionId);
        pollService.addVote(voteDTO);

        return "redirect:/poll/{id}";
    }

    @PostMapping("/{id}/delete")
    public String deletePoll(@PathVariable UUID id){
        pollService.delete(id);

        return "redirect:/polls";
    }

    @GetMapping("/{id}")
    public String getPoll(@PathVariable UUID id, Model model, Authentication authentication){
        try{
            User user = (User) authentication.getPrincipal();
            UUID userId = user.getId();

            PollResponseDTO poll = pollService.getPollWithUserVote(id, userId);
            model.addAttribute("authentication", authentication);
            model.addAttribute("poll", poll);
            model.addAttribute("userId", userId);

        } catch (Exception e){
            PollResponseDTO poll = pollService.getPoll(id);
            model.addAttribute("authentication", authentication);
            model.addAttribute("poll", poll);
        }

        return "poll/poll-details";
    }

    private PollDTO convertToDto(PollForm form) {
        List<OptionDTO> optionDTOs = form.getOptions().stream()
                .map(opt -> new OptionDTO(opt.getText(), null))
                .toList();

        return new PollDTO(
                form.getQuestion(),
                form.getDuration(),
                TimeUnit.valueOf(form.getTimeUnit()),
                optionDTOs,
                form.getVisibility()
        );
    }

    private Map<String, String> getTimeUnits() {
        return Map.of(
                "MINUTES", "Minutos",
                "HOURS", "Horas",
                "DAYS", "Dias"
        );
    }
}
