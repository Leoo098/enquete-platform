package com.project.enquete.core.enquete_platform.service;

import com.project.enquete.core.enquete_platform.model.Poll;
import com.project.enquete.core.enquete_platform.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository repository;

    public Poll save(Poll poll){
        return repository.save(poll);
    }

    public void delete(Poll poll){
        repository.delete(poll);
    }

    public Optional<Poll> getById(UUID id){
        return repository.findById(id);
    }
}
