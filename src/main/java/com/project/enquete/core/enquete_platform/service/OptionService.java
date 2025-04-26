package com.project.enquete.core.enquete_platform.service;

import com.project.enquete.core.enquete_platform.model.Option;
import com.project.enquete.core.enquete_platform.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository repository;

    public void save(Option option){
        repository.save(option);
    }

}
