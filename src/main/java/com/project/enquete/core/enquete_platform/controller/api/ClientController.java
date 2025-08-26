package com.project.enquete.core.enquete_platform.controller.api;

import com.project.enquete.core.enquete_platform.dto.request.ClientDTO;
import com.project.enquete.core.enquete_platform.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public void save(@RequestBody ClientDTO dto){
        service.save(dto);
    }
}
