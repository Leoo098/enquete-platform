package com.project.enquete.core.enquete_platform.controller.api;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

public interface GenericController {

    default URI generateHeaderLocation(UUID id){
        return ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{pollId}")
                .buildAndExpand(id)
                .toUri();
    }
}
