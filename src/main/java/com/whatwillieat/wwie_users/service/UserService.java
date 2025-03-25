package com.whatwillieat.wwie_users.service;

import com.whatwillieat.wwie_users.dto.*;
import com.whatwillieat.wwie_users.exception.UserNotAuthenticatedException;
import com.whatwillieat.wwie_users.exception.UserNotFoundException;
import com.whatwillieat.wwie_users.model.User;
import com.whatwillieat.wwie_users.repository.UserRepository;
import com.whatwillieat.wwie_users.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import com.whatwillieat.wwie_users.model.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public String registerUser(UserRegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null || userRepository.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("Username or email already taken");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profilePictureLink(request.getProfilePictureUrl())
                .build();

        userRepository.save(user);

        return jwtUtil.generateToken(user.getId().toString());
    }

    public String loginUser(UserLoginRequest request) {

        User user = authenticateUser(request.getEmail(), request.getPassword());

        if (user.isDeleted()) {
            throw new UserNotFoundException(user.getId());
        }

        return generateToken(user);
    }

    public String authenticateAndGenerateJwt(String email, String password) {
        User user = authenticateUser(email, password);
        return generateToken(user);
    }

    public User authenticateUser(String email, String password) {
        User user = getUserByEmailOrThrow(email);

        if(user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        throw new UserNotAuthenticatedException();
    }

    public String generateToken(User user) {
        return JwtUtil.generateToken(user.getId().toString());
    }

    public boolean isUserAdmin(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> UserRole.ADMIN == user.getUserRole())
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

    public User getUserByEmailOrThrow(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException());

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
                .userRole(user.getUserRole())
                .createdOn(user.getCreatedOn())
                .updatedOn(user.getUpdatedOn())
                .build();
    }

    public List<UserFullDataResponse> getAllUsers() {
        return userRepository.findAll().stream().map(user -> UserFullDataResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePictureLink(user.getProfilePictureLink())
                .isDeleted(user.isDeleted())
                .createdOn(user.getCreatedOn())
                .updatedOn(user.getUpdatedOn())
                .build()).collect(Collectors.toList());
    }
}