package com.project.enquete.core.enquete_platform.service;

import com.project.enquete.core.enquete_platform.controller.dto.request.UserDTO;
import com.project.enquete.core.enquete_platform.controller.mappers.UserMapper;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;

    public void save(UserDTO userDTO){
        User user = mapper.toEntity(userDTO);
        var password = user.getPassword();
        user.setPassword(encoder.encode(password));
        repository.save(user);
    }

    public void delete(UUID id){
        var user = repository.findById(id).orElseThrow();
        repository.delete(user);
    }

    public User findByEmail(String email){
        return repository.findByEmail(email);
    }
}
