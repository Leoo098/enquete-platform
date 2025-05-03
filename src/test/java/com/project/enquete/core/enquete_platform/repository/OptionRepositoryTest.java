package com.project.enquete.core.enquete_platform.repository;

import com.project.enquete.core.enquete_platform.model.Option;
import com.project.enquete.core.enquete_platform.model.Vote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class OptionRepositoryTest {

    @Autowired
    OptionRepository repository;
    @Autowired
    VoteRepository voteRepository;

    @Test
    public void saveTest(){
        List<Option> list = new ArrayList<>();

        Option opt1 = new Option();
        opt1.setText("opção 1");
        opt1.setPoll(null);

        Option opt2 = new Option();
        opt2.setText("opção 2");
        opt2.setPoll(null);

        list.add(opt1);
        list.add(opt2);

        list.forEach(repository::save);
    }

    @Test
    @Transactional
    @Commit
    public void deleteTest(){

        var findOption = repository.findById(4L);

        var id = findOption.getId();

        repository.deleteById(id);

        //assertFalse(repository.existsById(optionId));

        System.out.println("Opção removida: " + findOption);
    }

    @Test
    @Transactional
    public void returnVotes(){
        List<Vote> list = voteRepository.findAll();

        var votes = list.stream()
                .filter(v -> v.getOption().getId() == 5L)
                .collect(Collectors.toList());

        votes.forEach(System.out::println);
    }


}
