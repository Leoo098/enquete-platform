package com.project.enquete.core.enquete_platform.controller.api;

import com.project.enquete.core.enquete_platform.dto.request.UserDTO;
import com.project.enquete.core.enquete_platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid UserDTO dto) {
        userService.save(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
