package com.whatwillieat.wwie_users.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User not found");
    }

    public UserNotFoundException(UUID userId) {
        super("User with ID " + userId + " not found");
    }
}
