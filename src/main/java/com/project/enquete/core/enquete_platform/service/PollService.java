package com.project.enquete.core.enquete_platform.service;

import com.project.enquete.core.enquete_platform.controller.dto.request.PollDTO;
import com.project.enquete.core.enquete_platform.controller.dto.request.VoteDTO;
import com.project.enquete.core.enquete_platform.controller.dto.response.OptionResponseDTO;
import com.project.enquete.core.enquete_platform.controller.dto.response.PollResponseDTO;
import com.project.enquete.core.enquete_platform.controller.mappers.PollMapper;
import com.project.enquete.core.enquete_platform.model.Option;
import com.project.enquete.core.enquete_platform.model.Poll;
import com.project.enquete.core.enquete_platform.model.Vote;
import com.project.enquete.core.enquete_platform.repository.OptionRepository;
import com.project.enquete.core.enquete_platform.repository.PollRepository;
import com.project.enquete.core.enquete_platform.repository.VoteRepository;
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
    private final PollMapper mapper;

    public PollResponseDTO createPoll(PollDTO pollDTO){
        Poll poll = mapper.toEntity(pollDTO);
        poll.getOptions().forEach(option -> option.setPoll(poll));
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
        vote.setOption(option);
        vote.setVotedAt(Instant.now());

        voteRepository.save(vote);
    }
}
