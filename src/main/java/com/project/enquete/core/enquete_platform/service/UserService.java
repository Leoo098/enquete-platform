package com.project.enquete.core.enquete_platform.service;

import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User save(User user){
       return userRepository.save(user);
    }
}
