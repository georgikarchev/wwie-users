package com.whatwillieat.wwie_users.dto;

import com.whatwillieat.wwie_users.model.UserRole;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private UserRole userRole;
}

