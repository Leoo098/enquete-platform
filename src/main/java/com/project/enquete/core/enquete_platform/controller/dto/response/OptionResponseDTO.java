package com.project.enquete.core.enquete_platform.controller.dto.response;

public record OptionResponseDTO(
        Long id,
        String text,
        Integer votes
) {
}
