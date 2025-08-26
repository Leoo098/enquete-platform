package com.project.enquete.core.enquete_platform.dto.response;

public record OptionResponseDTO(
        Long id,
        String text,
        Integer votes
) {
}
