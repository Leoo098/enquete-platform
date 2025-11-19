package com.project.enquete.core.enquete_platform.service;

import com.project.enquete.core.enquete_platform.controller.mappers.UserMapper;
import com.project.enquete.core.enquete_platform.dto.request.UserDTO;
import com.project.enquete.core.enquete_platform.dto.form.PasswordForm;
import com.project.enquete.core.enquete_platform.dto.form.UserForm;
import com.project.enquete.core.enquete_platform.dto.form.UsernameForm;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.repository.UserRepository;
import com.project.enquete.core.enquete_platform.security.auth.CustomAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;

    public void save(UserDTO userDTO){
        User user = mapper.toEntity(userDTO);
        var password = user.getPassword();
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
    }

    public void delete(UUID id){
        var user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
    }

    public User findByEmail(String email){
        return userRepository.findByEmailIgnoreCase(email);
    }

    public User findByUsername(String login) {
        return userRepository.findByUsername(login);
    }

    public User updateUsername(UsernameForm usernameForm){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = (User) authentication.getPrincipal();
        User user = userRepository.findByEmailIgnoreCase(userPrincipal.getEmail());

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
        Authentication newAuth = new CustomAuthentication(updateUser);

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    public UserDTO convertToDto(UserForm userForm) {
        return new UserDTO(
                userForm.getUsername(),
                userForm.getEmail(),
                userForm.getPassword(),
                userForm.getPasswordConfirmation()
        );
    }
}
