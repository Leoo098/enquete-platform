package com.project.enquete.core.enquete_platform.dto.validator;

import com.project.enquete.core.enquete_platform.dto.response.ValidationError;
import com.project.enquete.core.enquete_platform.exceptions.UniqueFieldException;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository repository;

    public void validateUser(User user, BindingResult errors) {

        if (repository.existsByUsername(user.getUsername())){
            errors.rejectValue("username", "error.user", "Nome de usuário já cadastrado!");
        }

        if (repository.existsByEmail(user.getEmail())){
            errors.rejectValue("email", "error.user", "Email já cadastrado!");
        }

//        List<ValidationError> errors = new ArrayList<>();
//
//        if (repository.existsByEmail(user.getEmail())) {
//            errors.add(new ValidationError("email", "Email já cadastrado!"));
//        }
//
//        if (repository.existsByUsername(user.getUsername())) {
//            errors.add(new ValidationError("username", "Nome de usuário já cadastrado!"));
//        }
//
//        if (!errors.isEmpty()) {
//            throw new UniqueFieldException(errors);
//        }
    }
}
