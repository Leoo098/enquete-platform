package com.project.enquete.core.enquete_platform.service;

import com.project.enquete.core.enquete_platform.dto.request.OptionDTO;
import com.project.enquete.core.enquete_platform.dto.request.PollDTO;
import com.project.enquete.core.enquete_platform.dto.request.VoteDTO;
import com.project.enquete.core.enquete_platform.dto.response.OptionResponseDTO;
import com.project.enquete.core.enquete_platform.dto.response.PollResponseDTO;
import com.project.enquete.core.enquete_platform.controller.mappers.PollMapper;
import com.project.enquete.core.enquete_platform.dto.form.PollForm;
import com.project.enquete.core.enquete_platform.model.*;
import com.project.enquete.core.enquete_platform.repository.OptionRepository;
import com.project.enquete.core.enquete_platform.repository.PollRepository;
import com.project.enquete.core.enquete_platform.repository.VoteRepository;
import com.project.enquete.core.enquete_platform.repository.projection.VoteInfoProjection;
import com.project.enquete.core.enquete_platform.security.services.SecurityService;
import com.project.enquete.core.enquete_platform.service.dto.PollVoteInfo;
import com.project.enquete.core.enquete_platform.service.dto.UserVoteInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final OptionRepository optionRepository;
    private final SecurityService securityService;
    private final PollMapper mapper;
    private final UserService userService;

    public PollResponseDTO createPoll(PollDTO pollDTO){
        Poll poll = mapper.toEntity(pollDTO);
        User user = securityService.getLoggedInUser();
        poll.getOptions().forEach(option -> option.setPoll(poll));
        poll.setCreatedBy(user);
        pollRepository.save(poll);

        return getPoll(poll.getId());
    }

    public void delete(UUID id){
        pollRepository.deleteById(id);
    }

    public PollResponseDTO getPoll(UUID id){
        Poll poll = pollRepository.findById(id).orElse(null);

        return getResult(poll);
    }

    public Page<PollResponseDTO> getAllPolls(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UUID userId = userService.findByUsername(username).getId();

        Page<Poll> polls = pollRepository.findAllWithOptionsForCurrentUser(userId, pageable);

        return polls.map(this::getResult);
    }

    public Page<PollResponseDTO> getVotedPolls(Pageable pageable){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UUID userId = userService.findByUsername(username).getId();

        Page<Poll> polls = pollRepository.findVotedPolls(userId, pageable);

        return polls.map(this::getResult);
    }

    public List<PollResponseDTO> getRandomPublicPolls(){
        List<Poll> RandomPublicPolls = pollRepository.findRandomPublicPolls();

        return RandomPublicPolls.stream()
                .map(this::getResult)
                .toList();
    }

    private PollResponseDTO getResult(Poll poll){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        PollVoteInfo info = getPollVoteInfo(poll.getId());
        UserVoteInfo userVoteInfo = getUserVoteInfo(authentication, poll.getId());

        List<OptionResponseDTO> options = calculateOptionsWithPercentages(
                poll.getOptions(), info.votesByOption(), info.totalVotes()
        );

        List<Long> winnerOptionIds = findWinnerOptionIds(info.votesByOption());

        return buildPollResponseDTO(poll, options, info.totalVotes(), userVoteInfo, winnerOptionIds);
    }

    private PollVoteInfo getPollVoteInfo(UUID pollId){
        List<VoteInfoProjection> results = voteRepository.findVoteInfo(pollId);

        Map<Long, Integer> votesByOption = results.stream()
                .collect(Collectors.toMap(
                        VoteInfoProjection::getOptionId,
                        p -> p.getVoteCount().intValue()
                ));

        int totalVotes = votesByOption.values().stream().mapToInt(Integer::intValue).sum();

        return new PollVoteInfo(totalVotes, votesByOption);
    }

    private UserVoteInfo getUserVoteInfo(Authentication authentication, UUID pollId){
        if (!authentication.isAuthenticated()){
            return new UserVoteInfo(null, false);
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        if (user == null) {
            return new UserVoteInfo(null, false);
        }

        Long voteOptionId = voteRepository.findVotedOptionByUserAndPoll(user.getId(), pollId);

        return new UserVoteInfo(voteOptionId, voteOptionId != null);
    }

    private List<OptionResponseDTO> calculateOptionsWithPercentages(
            List<Option> options, Map<Long, Integer> votesByOption, int totalVotes){

        return options.stream()
                .map(option -> {
                        int votes = votesByOption.getOrDefault(option.getId(), 0);
                        double percentage = totalVotes > 0 ? (votes * 100.0) / totalVotes : 0.0;
                        return new OptionResponseDTO(option.getId(), option.getText(), votes, percentage);
                    })
                .toList();
    }

    private List<Long> findWinnerOptionIds(Map<Long, Integer> votesByOption){
        if (votesByOption.isEmpty()) return List.of();

        int maxVotes = Collections.max(votesByOption.values());
        if (maxVotes <= 0) return List.of();

        return votesByOption.entrySet().stream()
                .filter(entry -> entry.getValue() == maxVotes)
                .map(Map.Entry::getKey)
                .toList();
    }

    private PollResponseDTO buildPollResponseDTO(Poll poll, List<OptionResponseDTO> options, Integer totalVotes, UserVoteInfo userVoteInfo, List<Long> winnerOptionIds){
        return new PollResponseDTO(
                poll.getId(),
                poll.getQuestion(),
                poll.getCreatedAt(),
                poll.getExpiresAt(),
                calculateTimeLeft(Instant.now(), poll.getExpiresAt()),
                options,
                totalVotes,
                poll.getCreatedBy().getUsername(),
                poll.getVisibility(),
                poll.getCreatedBy().getId(),
                userVoteInfo.votedOptionId(),
                userVoteInfo.hasVoted(),
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

    public PollDTO convertToDto(PollForm form) {
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

    public void addVote(VoteDTO dto){
        Option option = optionRepository.findById(dto.optionId());
        Vote vote = new Vote();
        User user = securityService.getLoggedInUser();
        vote.setUser(user);
        vote.setOption(option);
        vote.setVotedAt(Instant.now());

        voteRepository.save(vote);
    }
}
