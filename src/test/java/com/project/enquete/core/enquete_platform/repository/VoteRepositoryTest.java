package com.project.enquete.core.enquete_platform.repository;

import com.project.enquete.core.enquete_platform.model.Option;
import com.project.enquete.core.enquete_platform.model.Vote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@SpringBootTest
public class VoteRepositoryTest {

    @Autowired
    VoteRepository repository;
    @Autowired
    OptionRepository optionRepository;

    @Test
    @Transactional
    @Commit
    public void saveTest(){

        Option option = optionRepository.findById(6L);

        Vote vote = new Vote();
        vote.setUser(null);
        vote.setOption(option);
        vote.setVotedAt(Instant.now());
        vote.setDeviceToken(null);

        var voted = repository.save(vote);
        System.out.println("Voto contabilizado: " + voted);
    }
}
