package com.project.enquete.core.enquete_platform.dto.validator;

import com.project.enquete.core.enquete_platform.exceptions.ExpiredPollException;
import com.project.enquete.core.enquete_platform.model.Poll;
import com.project.enquete.core.enquete_platform.model.Vote;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VoteValidator {

    public void validateVote(Vote vote){
        Poll poll = vote.getOption().getPoll();

        if (vote.getVotedAt().isAfter(poll.getExpiresAt())){
            throw new ExpiredPollException("Enquete finalizada.");
        }
    }
}
