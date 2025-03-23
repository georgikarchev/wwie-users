package com.whatwillieat.wwie_users.web;

import com.whatwillieat.wwie_users.dto.UserLoginRequest;
import com.whatwillieat.wwie_users.dto.UserRegistrationRequest;
import com.whatwillieat.wwie_users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

