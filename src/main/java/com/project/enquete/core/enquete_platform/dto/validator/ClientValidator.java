package com.project.enquete.core.enquete_platform.dto.validator;

import com.project.enquete.core.enquete_platform.dto.response.ValidationError;
import com.project.enquete.core.enquete_platform.exceptions.UniqueFieldException;
import com.project.enquete.core.enquete_platform.model.Client;
import com.project.enquete.core.enquete_platform.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ClientValidator {

    private final ClientRepository repository;

    public void validateClient(Client client) {
        List<ValidationError> errors = new ArrayList<>();

        if (repository.existsByClientId(client.getClientId())) {
            errors.add(new ValidationError("clientId", "ClientId já cadastrado!"));
        }

        if (!errors.isEmpty()) {
            throw new UniqueFieldException(errors);
        }
    }
}
