package com.whatwillieat.wwie_users.service;

import com.whatwillieat.wwie_users.dto.UpdateUserRequest;
import com.whatwillieat.wwie_users.dto.UserResponse;
import com.whatwillieat.wwie_users.exception.UserNotFoundException;
import com.whatwillieat.wwie_users.model.User;
import com.whatwillieat.wwie_users.repository.UserRepository;
import com.whatwillieat.wwie_users.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import com.whatwillieat.wwie_users.model.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
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
                .map(user -> "ADMIN".equals(user.getUserRole()))
                .orElse(false);
    }

    public void updateUserRole(UUID userId, UserRole userRole) {
        User user = getUserOrThrow(userId);

        user.setUserRole(userRole);
        userRepository.save(user);
    }

    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        User user = getUserOrThrow(userId);

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePictureLink(user.getProfilePictureLink())
                .createdOn(user.getCreatedOn())
                .updatedOn(user.getUpdatedOn())
                .build();
    }

    public void softDeleteUser(UUID userId) {
        User user = getUserOrThrow(userId);
        user.setDeleted(true);
        userRepository.save(user);
    }

    public void deleteUser(UUID userId) {
        User user = getUserOrThrow(userId);
        userRepository.delete(user);
    }

    public User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public UserResponse getNonSoftDeletedUserById(UUID userId) {
        User user = getUserOrThrow(userId);

        if (user.isDeleted()) {
            throw new UserNotFoundException(userId);
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePictureLink(user.getProfilePictureLink())
                .createdOn(user.getCreatedOn())
                .updatedOn(user.getUpdatedOn())
                .build();
    }
}