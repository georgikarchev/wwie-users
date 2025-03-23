package com.whatwillieat.wwie_users.service;

import com.whatwillieat.wwie_users.model.User;
import com.whatwillieat.wwie_users.repository.UserRepository;
import com.whatwillieat.wwie_users.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Register a user
    public String registerUser(String username, String email, String password, String profilePictureLink) {
        if (userRepository.findByUsername(username) != null || userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Username or email already taken");
        }

        userRepository.save(User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .profilePictureLink(profilePictureLink)
                .build());

        return jwtUtil.generateToken(username); // Return the JWT after successful registration
    }
}