package com.project.enquete.core.enquete_platform.dto.validator;

import com.project.enquete.core.enquete_platform.dto.request.UserDTO;
import com.project.enquete.core.enquete_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository repository;

    public void validateUser(UserDTO user, BindingResult errors) {

        if (repository.existsByUsernameIgnoreCase(user.username())){
            errors.rejectValue("username", "error.user", "Nome de usuário já cadastrado!");
        }

        if (repository.existsByEmail(user.email())){
            errors.rejectValue("email", "error.user", "Email já cadastrado!");
        }
    }
}
