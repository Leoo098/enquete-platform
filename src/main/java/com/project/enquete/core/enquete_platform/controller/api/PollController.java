package com.project.enquete.core.enquete_platform.controller.api;

import com.project.enquete.core.enquete_platform.dto.request.PollDTO;
import com.project.enquete.core.enquete_platform.dto.request.VoteDTO;
import com.project.enquete.core.enquete_platform.dto.response.PollResponseDTO;
import com.project.enquete.core.enquete_platform.service.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/poll")
@RequiredArgsConstructor
public class PollController implements GenericController{

    private final PollService pollService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PollResponseDTO> save(@RequestBody @Valid PollDTO dto){
        PollResponseDTO response = pollService.createPoll(dto);
        URI location = generateHeaderLocation(response.id());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id){
        pollService.delete(id);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("{id}")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
//    public ResponseEntity<Void> addVote(@RequestBody VoteDTO dto){
//        pollService.addVote(dto);
//        return ResponseEntity.ok().build();
//    }

//    @GetMapping("{id}")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
//    public ResponseEntity<PollResponseDTO> getPoll(@PathVariable UUID id){
//        var poll = pollService.getPoll(id);
//        return ResponseEntity.ok().body(poll);
//    }
}
