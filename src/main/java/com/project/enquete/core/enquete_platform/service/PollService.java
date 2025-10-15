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
import java.util.*;

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

        int totalVotes = voteRepository.countVotesByPollId(poll.getId());
        List<OptionResponseDTO> optionsWithPercentage = new ArrayList<>();
        List<Long> winnerOptionIds = new ArrayList<>();
        int maxVotes = -1;

        for (var option : poll.getOptions()) {
            int votes = option.getVotes().size();

            double percentage = totalVotes > 0 ? (votes * 100.0) / totalVotes : 0.0;
            OptionResponseDTO optionDTO = new OptionResponseDTO(
                    option.getId(), option.getText(), votes, percentage
            );
            optionsWithPercentage.add(optionDTO);

            if (votes > maxVotes) {
                maxVotes = votes;
                winnerOptionIds.clear();
                winnerOptionIds.add(option.getId());
            } else if (votes == maxVotes && votes > 0) {
                winnerOptionIds.add(option.getId());
            }
        }

        if (maxVotes <= 0) {
            winnerOptionIds.clear();
        }

        return getPollResponseDTO(poll, optionsWithPercentage, totalVotes, winnerOptionIds);
    }

    public List<PollResponseDTO> getAllPolls() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UUID userId = userService.findByUsername(username).getId();

        List<Poll> polls = pollRepository.findAllWithOptionsForCurrentUser(userId);

        return polls.stream()
                .map(poll -> {
                    int totalVotes = voteRepository.countVotesByPollId(poll.getId());
                    List<OptionResponseDTO> optionsWithPercentage = new ArrayList<>();
                    List<Long> winnerOptionIds = new ArrayList<>();
                    int maxVotes = -1;

                    for (var option : poll.getOptions()){
                        int votes = voteRepository.countVotesByOptionId(option.getId());

                        double percentage = totalVotes > 0 ? (votes * 100.0) / totalVotes : 0.0;

                        OptionResponseDTO optionDTO = new OptionResponseDTO(
                                option.getId(), option.getText(), votes, percentage
                        );
                        optionsWithPercentage.add(optionDTO);

                        if (votes > maxVotes){
                            maxVotes = votes;
                            winnerOptionIds.clear();
                            winnerOptionIds.add(option.getId());
                        }
                        else if (votes == maxVotes && votes > 0) {
                            winnerOptionIds.add(option.getId());
                        }
                    }

                    if (maxVotes <= 0){
                        winnerOptionIds.clear();
                    }

                    return getPollResponseDTO(poll, optionsWithPercentage, totalVotes, winnerOptionIds);
                })
                .toList();
    }

    public List<PollResponseDTO> getRandomPublicPolls(){
        List<Poll> RandomPublicPolls = pollRepository.findRandomPublicPolls();

        return RandomPublicPolls.stream()
                .map(poll -> {
                    int totalVotes = voteRepository.countVotesByPollId(poll.getId());
                    List<OptionResponseDTO> optionsWithPercentage = new ArrayList<>();

                    for (var option : poll.getOptions()){
                        int votes = voteRepository.countVotesByOptionId(option.getId());

                        double percentage = totalVotes > 0 ? (votes * 100.0) / totalVotes : 0.0;

                        OptionResponseDTO optionDTO = new OptionResponseDTO(
                                option.getId(), option.getText(), votes, percentage
                        );
                        optionsWithPercentage.add(optionDTO);
                    }

                    return getPollResponseDTO(poll, optionsWithPercentage, totalVotes, null);
                })
                .toList();
    }

    private PollResponseDTO getPollResponseDTO(Poll poll, List<OptionResponseDTO> optionsWithPercentage, int totalVotes, List<Long> winnerOptionIds) {
        return new PollResponseDTO(
                poll.getId(),
                poll.getQuestion(),
                poll.getCreatedAt(),
                poll.getExpiresAt(),
                calculateTimeLeft(Instant.now(), poll.getExpiresAt()),
                optionsWithPercentage,
                totalVotes,
                poll.getCreatedBy().getUsername(),
                poll.getVisibility(),
                poll.getCreatedBy().getId(),
                null,
                false,
                Instant.now().isAfter(poll.getExpiresAt()),
                winnerOptionIds
        );
    }

    private String calculateTimeLeft(Instant now, Instant expiresAt) {
        if (now.isAfter(expiresAt)) {
            return "Finalizada";
        }

        Duration duration = Duration.between(now, expiresAt);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        return "Restam " + hours + "h " + minutes + "m";
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
                originalPoll.visibility(),
                originalPoll.userId(),
                userVoteOptionId,
                userVoteOptionId != null,
                originalPoll.isExpired(),
                originalPoll.winnerOptionIds()
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
