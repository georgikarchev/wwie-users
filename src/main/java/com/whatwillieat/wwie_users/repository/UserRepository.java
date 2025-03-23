package com.whatwillieat.wwie_users.repository;

import com.whatwillieat.wwie_users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
}

