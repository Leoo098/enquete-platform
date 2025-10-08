package com.project.enquete.core.enquete_platform.dto.validator;

import com.project.enquete.core.enquete_platform.exceptions.AlreadyVotedException;
import com.project.enquete.core.enquete_platform.exceptions.ExpiredPollException;
import com.project.enquete.core.enquete_platform.model.Poll;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.model.Vote;
import com.project.enquete.core.enquete_platform.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VoteValidator {

    private final VoteRepository voteRepository;

    public void validateVote(Vote vote){
        UUID pollId = vote.getOption().getPoll().getId();
        UUID userId = vote.getUser().getId();

//        if (vote.getVotedAt().isAfter(poll.getExpiresAt())){
//            throw new ExpiredPollException("Enquete finalizada.");
//        }
        if (voteRepository.existsByUserIdAndPollId(userId, pollId)){
            throw new AlreadyVotedException("Já votou nesta enquete.");
        }
    }

}
