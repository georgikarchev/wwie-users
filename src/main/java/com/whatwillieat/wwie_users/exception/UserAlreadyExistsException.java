package com.whatwillieat.wwie_users.exception;

public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException() {
    super("Username or email already exist");
  }
}
