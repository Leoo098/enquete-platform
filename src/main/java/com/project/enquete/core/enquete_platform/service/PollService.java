package com.project.enquete.core.enquete_platform.service;

import com.project.enquete.core.enquete_platform.controller.dto.request.PollDTO;
import com.project.enquete.core.enquete_platform.controller.dto.request.VoteDTO;
import com.project.enquete.core.enquete_platform.controller.dto.response.OptionResponseDTO;
import com.project.enquete.core.enquete_platform.controller.dto.response.PollResponseDTO;
import com.project.enquete.core.enquete_platform.controller.dto.validator.VoteValidator;
import com.project.enquete.core.enquete_platform.controller.mappers.PollMapper;
import com.project.enquete.core.enquete_platform.model.Option;
import com.project.enquete.core.enquete_platform.model.Poll;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.model.Vote;
import com.project.enquete.core.enquete_platform.repository.OptionRepository;
import com.project.enquete.core.enquete_platform.repository.PollRepository;
import com.project.enquete.core.enquete_platform.repository.VoteRepository;
import com.project.enquete.core.enquete_platform.security.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final OptionRepository optionRepository;
    private final SecurityService securityService;
    private final PollMapper mapper;
    private final VoteValidator voteValidator;

    public PollResponseDTO createPoll(PollDTO pollDTO){
        Poll poll = mapper.toEntity(pollDTO);
        User user = securityService.getLoggedInUser();
        poll.getOptions().forEach(option -> option.setPoll(poll));
        poll.setCreatedBy(user);
        pollRepository.save(poll);

        return mapper.toResponseDTO(poll, pollDTO);
    }

    public void delete(UUID id){
        pollRepository.deleteById(id);
    }

    public PollResponseDTO getPoll(UUID id){
        Poll poll = pollRepository.findById(id).orElse(null);

        return new PollResponseDTO(
                poll.getId(),
                poll.getQuestion(),
                poll.getCreatedAt(),
                poll.getExpiresAt(),
                Duration.between(Instant.now(), poll.getExpiresAt()),
                poll.getOptions()
                        .stream()
                        .map( option -> new OptionResponseDTO(option.getId(), option.getText(), option.getVotes().size()))
                        .toList(
        ));
    }

    public void addVote(VoteDTO dto){
        Option option = optionRepository.findById(dto.optionId());
        Vote vote = new Vote();
        User user = securityService.getLoggedInUser();
        vote.setUser(user);
        vote.setOption(option);
        vote.setVotedAt(Instant.now());
        voteValidator.validateVote(vote);

        voteRepository.save(vote);
    }
}
