package com.project.enquete.core.enquete_platform.controller;

import com.project.enquete.core.enquete_platform.controller.dto.request.UserDTO;
import com.project.enquete.core.enquete_platform.controller.mappers.UserMapper;
import com.project.enquete.core.enquete_platform.model.User;
import com.project.enquete.core.enquete_platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

private final UserService userService;
private final UserMapper mapper;

@PostMapping
public ResponseEntity<User> createUser(@RequestBody @Valid UserDTO dto){
    User user = mapper.toEntity(dto);
    var save = userService.save(user);

    return ResponseEntity.ok().body(save);
}
}
