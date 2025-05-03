package com.project.enquete.core.enquete_platform.repository;

import com.project.enquete.core.enquete_platform.repository.projection.OptionStats;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
public class PollRepositoryTest {

    @Autowired
    PollRepository repository;

    @Test
    public void getPoll(){
        List<OptionStats> pollResults = repository.getPollResults(UUID.fromString("c6e07276-74c5-4029-ab0e-5aa1e28b4102"));


        pollResults.forEach(os -> System.out.printf(
                "OptionStats[optionId=%s, text='%s', votes=%d, percentage=%.1f%%]%n",
                os.getId(), os.getText(), os.getVoteCount(), os.getPercentage()
        ));
    }
}
