package com.project.enquete.core.enquete_platform.repository;

import com.project.enquete.core.enquete_platform.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Client findByClientId(String clientId);

    boolean existsByClientId(String clientId);
}
