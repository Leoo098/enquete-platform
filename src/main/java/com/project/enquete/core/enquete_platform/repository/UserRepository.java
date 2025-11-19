package com.project.enquete.core.enquete_platform.repository;

import com.project.enquete.core.enquete_platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmail(String email);

    User findByUsername(String login);
}
