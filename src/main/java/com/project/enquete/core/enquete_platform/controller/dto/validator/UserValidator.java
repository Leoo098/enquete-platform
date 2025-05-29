package com.project.enquete.core.enquete_platform.controller.dto.validator;

import com.project.enquete.core.enquete_platform.controller.dto.response.ValidationError;
import com.project.enquete.core.enquete_platform.exceptions.UniqueFieldException;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository repository;

    public void validateUser(User user) {
        List<ValidationError> errors = new ArrayList<>();

        if (repository.existsByEmail(user.getEmail())) {
            errors.add(new ValidationError("email", "Email já cadastrado!"));
        }

        if (repository.existsByUsername(user.getUsername())) {
            errors.add(new ValidationError("username", "Nome de usuário já cadastrado!"));
        }

        if (!errors.isEmpty()) {
            throw new UniqueFieldException(errors);
        }
    }
}
