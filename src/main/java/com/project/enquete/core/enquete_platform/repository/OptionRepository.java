package com.project.enquete.core.enquete_platform.repository;

import com.project.enquete.core.enquete_platform.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OptionRepository extends JpaRepository<Option, UUID> {

    Option findById(Long id);

    void deleteById(Long id);
}
