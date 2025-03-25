package com.whatwillieat.wwie_users.exception;

public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException() {
        super("Could not authenticate user");
    }
}
