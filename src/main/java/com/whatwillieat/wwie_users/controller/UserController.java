package com.whatwillieat.wwie_users.controller;

import com.whatwillieat.wwie_users.dto.*;
import com.whatwillieat.wwie_users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${app.API_V1_BASE_URL}/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        return userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword(), request.getProfilePictureUrl());
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody UserLoginRequest request) {
        String jwt = userService.authenticateAndGenerateJwt(request.getEmail(), request.getPassword());

        if (jwt == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        return ResponseEntity.ok(jwt); // Return JWT token
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable UUID userId) {
        return userService.getNonSoftDeletedUserById(userId);
    }

    @GetMapping("/{id}/is-admin")
    public Map<String, Boolean> isAdmin(@PathVariable UUID id) {
        return Map.of("isAdmin", userService.isUserAdmin(id));
    }

    @PutMapping("/{userId}/role")
    public void updateUserRole(@PathVariable UUID userId, @RequestBody @Valid UpdateRoleRequest request) {
        userService.updateUserRole(userId, request.getUserRole());
    }

    @PutMapping("/{userId}")
    public UserResponse updateUser(@PathVariable UUID userId, @RequestBody @Valid UpdateUserRequest request) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable UUID userId) {
        userService.softDeleteUser(userId);
    }

}

