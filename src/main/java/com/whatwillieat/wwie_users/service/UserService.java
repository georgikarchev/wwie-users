package com.whatwillieat.wwie_users.service;

import com.whatwillieat.wwie_users.model.User;
import com.whatwillieat.wwie_users.repository.UserRepository;
import com.whatwillieat.wwie_users.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // Register a user
    public String registerUser(String username, String email, String password, String profilePictureLink) {
        if (userRepository.findByUsername(username) != null || userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Username or email already taken");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .profilePictureLink(profilePictureLink)
                .build();

        userRepository.save(user);

        return jwtUtil.generateToken(user.getId().toString());
    }

    public String authenticateAndGenerateJwt(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return JwtUtil.generateToken(user.getId().toString());
        }

        return null;
    }

    public boolean isUserAdmin(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> "ADMIN".equals(user.getRole()))
                .orElse(false);
    }
}