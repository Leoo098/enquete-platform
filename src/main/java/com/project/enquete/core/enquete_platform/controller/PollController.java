package com.project.enquete.core.enquete_platform.controller;

import com.project.enquete.core.enquete_platform.controller.dto.request.PollDTO;
import com.project.enquete.core.enquete_platform.controller.dto.response.PollResponseDTO;
import com.project.enquete.core.enquete_platform.controller.mappers.PollMapper;
import com.project.enquete.core.enquete_platform.model.Poll;
import com.project.enquete.core.enquete_platform.service.OptionService;
import com.project.enquete.core.enquete_platform.service.PollService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/polls")
@RequiredArgsConstructor
public class PollController implements GenericController{

    private final PollService pollService;
    private final OptionService optionService;
    private final PollMapper mapper;

    @PostMapping
    public ResponseEntity<PollResponseDTO> save(@RequestBody @Valid PollDTO dto){
        Poll poll = mapper.toEntity(dto);
        poll.getOptions().forEach(option -> option.setPoll(poll));
        pollService.save(poll);

        PollResponseDTO response = mapper.toResponseDTO(poll, dto);

        URI location = generateHeaderLocation(poll.getId());

        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") String id){
        var idPoll = UUID.fromString(id);
        Optional<Poll> pollOptional = pollService.getById(idPoll);

        if (pollOptional.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        pollService.delete(pollOptional.get());

        return ResponseEntity.noContent().build();
    }
}
