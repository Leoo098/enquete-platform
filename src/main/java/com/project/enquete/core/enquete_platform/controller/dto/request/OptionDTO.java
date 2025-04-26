package com.project.enquete.core.enquete_platform.controller.dto.request;

import java.util.UUID;

public record OptionDTO(String text, UUID pollId) {
}
