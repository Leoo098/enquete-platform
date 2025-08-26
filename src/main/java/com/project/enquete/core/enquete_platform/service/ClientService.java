package com.project.enquete.core.enquete_platform.service;

import com.project.enquete.core.enquete_platform.dto.request.ClientDTO;
import com.project.enquete.core.enquete_platform.dto.validator.ClientValidator;
import com.project.enquete.core.enquete_platform.controller.mappers.ClientMapper;
import com.project.enquete.core.enquete_platform.model.Client;
import com.project.enquete.core.enquete_platform.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;
    private final PasswordEncoder encoder;
    private final ClientValidator validator;

    public Client save(ClientDTO clientDTO){
        Client client = mapper.toEntity(clientDTO);
        validator.validateClient(client);
        client.setClientSecret(encoder.encode(client.getClientSecret()));
        return repository.save(client);
    }

    public Client getByClientId(String clientId){
        return repository.findByClientId(clientId);
    }

}
