package com.project.enquete.core.enquete_platform.service;

import com.project.enquete.core.enquete_platform.dto.request.PollDTO;
import com.project.enquete.core.enquete_platform.dto.request.VoteDTO;
import com.project.enquete.core.enquete_platform.dto.response.OptionResponseDTO;
import com.project.enquete.core.enquete_platform.dto.response.PollResponseDTO;
import com.project.enquete.core.enquete_platform.dto.validator.VoteValidator;
import com.project.enquete.core.enquete_platform.controller.mappers.PollMapper;
import com.project.enquete.core.enquete_platform.model.Option;
import com.project.enquete.core.enquete_platform.model.Poll;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.model.Vote;
import com.project.enquete.core.enquete_platform.repository.OptionRepository;
import com.project.enquete.core.enquete_platform.repository.PollRepository;
import com.project.enquete.core.enquete_platform.repository.VoteRepository;
import com.project.enquete.core.enquete_platform.security.services.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final UserService userService;

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

        List<OptionResponseDTO> optionDTOs = poll.getOptions().stream()
                .map(option -> {
                    int votes = option.getVotes().size();
                    return new OptionResponseDTO(
                            option.getId(),
                            option.getText(),
                            votes,
                            0.0
                    );
                })
                .toList();

        int totalVotes = optionDTOs.stream().mapToInt(OptionResponseDTO::votes).sum();

        List<OptionResponseDTO> optionsWithPercentage = optionDTOs.stream()
                .map(option -> new OptionResponseDTO(
                        option.id(),
                        option.text(),
                        option.votes(),
                        totalVotes > 0 ? (option.votes() * 100.0) / totalVotes : 0.0
                ))
                .toList();

        return new PollResponseDTO(
                poll.getId(),
                poll.getQuestion(),
                poll.getCreatedAt(),
                poll.getExpiresAt(),
                Duration.between(Instant.now(), poll.getExpiresAt()),
                optionsWithPercentage,
                totalVotes,
                poll.getCreatedBy().getUsername(),
                poll.getCreatedBy().getId(),
                null,
                false
        );
    }

    public List<PollResponseDTO> getAllPolls() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UUID userId = userService.findByUsername(username).getId();

        List<Poll> polls = pollRepository.findAllWithOptionsForCurrentUser(userId);

        return polls.stream()
                .map(poll -> {
                    List<OptionResponseDTO> options = poll.getOptions().stream()
                        .map(option -> {
                            int votes = voteRepository.countVotesByOptionId(option.getId());
                            return new OptionResponseDTO(
                                    option.getId(),
                                    option.getText(),
                                    votes,
                                    0.0
                            );
                        })
                        .toList();

                    int totalVotes = voteRepository.countVotesByPollId(poll.getId());

                    List<OptionResponseDTO> optionsWithPercentage = options.stream()
                            .map(option -> new OptionResponseDTO(
                                    option.id(),
                                    option.text(),
                                    option.votes(),
                                    totalVotes > 0 ? (option.votes() * 100.0) / totalVotes : 0.0
                            ))
                            .toList();

                    return new PollResponseDTO(
                            poll.getId(),
                            poll.getQuestion(),
                            poll.getCreatedAt(),
                            poll.getExpiresAt(),
                            Duration.between(Instant.now(), poll.getExpiresAt()),
                            optionsWithPercentage,
                            totalVotes,
                            poll.getCreatedBy().getUsername(),
                            poll.getCreatedBy().getId(),
                            null,
                            false
                    );
                })
                .toList();
    }

    public Long getUserVoteOptionId(UUID pollId, UUID userId) {
        return voteRepository.findByUserIdAndOptionPollId(userId, pollId)
                .map(vote -> vote.getOption().getId())
                .orElse(null);
    }

    public PollResponseDTO getPollWithUserVote(UUID pollId, UUID userId) {
        PollResponseDTO originalPoll = getPoll(pollId);
        Long userVoteOptionId = getUserVoteOptionId(pollId, userId);

        return new PollResponseDTO(
                originalPoll.id(),
                originalPoll.question(),
                originalPoll.createdAt(),
                originalPoll.expiresAt(),
                originalPoll.timeLeft(),
                originalPoll.options(),
                originalPoll.totalVotes(),
                originalPoll.createdBy(),
                originalPoll.userId(),
                userVoteOptionId,
                userVoteOptionId != null
        );
    }

    public void addVote(VoteDTO dto){
        Option option = optionRepository.findById(dto.optionId());
        Vote vote = new Vote();
        User user = securityService.getLoggedInUser();
        vote.setUser(user);
        vote.setOption(option);
        vote.setVotedAt(Instant.now());
//        voteValidator.validateVote(vote);

        voteRepository.save(vote);
    }
}
