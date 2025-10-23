package com.project.enquete.core.enquete_platform.service;

import com.project.enquete.core.enquete_platform.dto.request.UserDTO;
import com.project.enquete.core.enquete_platform.dto.validator.UserValidator;
import com.project.enquete.core.enquete_platform.controller.mappers.UserMapper;
import com.project.enquete.core.enquete_platform.form.PasswordForm;
import com.project.enquete.core.enquete_platform.form.UserForm;
import com.project.enquete.core.enquete_platform.form.UsernameForm;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.repository.UserRepository;
import com.project.enquete.core.enquete_platform.security.auth.CustomAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;
    private final UserValidator validator;

    public void save(UserDTO userDTO){
        User user = mapper.toEntity(userDTO);
//        validator.validateUser(user);
        var password = user.getPassword();
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
    }

    public void delete(UUID id){
        var user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User findByUsername(String login) {
        return userRepository.findByUsername(login);
    }

    public User updateUsername(UsernameForm usernameForm){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = (User) authentication.getPrincipal();
        User user = userRepository.findByEmail(userPrincipal.getEmail());

//        User user = userRepository.findByUsername(username);

        user.setUsername(usernameForm.getUsername());

        return userRepository.save(user);
    }

    public boolean checkPassword(String typedPassword, Authentication authentication){
        String login = authentication.getName();
        User user = userRepository.findByUsername(login);
        String encryptedPassword = user.getPassword();

        return encoder.matches(typedPassword, encryptedPassword);
    }

    public void updatePassword(PasswordForm passwordForm){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        var password = encoder.encode(passwordForm.getPassword());
        user.setPassword(password);

        userRepository.save(user);
    }

    public void updateAuthentication(User updateUser){
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        Authentication newAuth = new CustomAuthentication(updateUser);

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public void validateUser(UserForm user, BindingResult errors) {

        if (userRepository.existsByUsername(user.getUsername())) {
            errors.rejectValue("username", "error.user", "Nome de usuário já cadastrado!");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            errors.rejectValue("email", "error.user", "Email já cadastrado!");
        }
    }
}
