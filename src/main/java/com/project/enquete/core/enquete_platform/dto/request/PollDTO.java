package com.project.enquete.core.enquete_platform.dto.request;

import com.project.enquete.core.enquete_platform.model.TimeUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PollDTO(
        @NotBlank(message = "Campo obrigatório")
        String question,
        @NotNull(message = "Campo obrigatório")
        long duration,
        @NotNull(message = "Campo obrigatório")
        TimeUnit timeUnit,
        List<OptionDTO> options,
        String visibility
){}
