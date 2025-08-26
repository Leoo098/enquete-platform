package com.project.enquete.core.enquete_platform.dto.request;

public record ClientDTO(String clientId,
        String clientSecret,
        String redirectURI,
        String scope) {
}
