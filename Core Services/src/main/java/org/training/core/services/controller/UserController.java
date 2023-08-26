package org.training.core.services.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.training.core.services.model.dto.Response;
import org.training.core.services.model.dto.UserDto;
import org.training.core.services.service.UserService;

@RestController
@RequestMapping("core-services/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{identificationNumber}")
    public ResponseEntity readUser(@PathVariable String identificationNumber) {
        return ResponseEntity.ok(userService.readUser(identificationNumber));
    }

    @GetMapping
    public ResponseEntity readUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.readUsers(pageable));
    }

    @PostMapping
    public ResponseEntity<Response> createUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);
    }
}