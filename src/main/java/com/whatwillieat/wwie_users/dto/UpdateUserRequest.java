package com.whatwillieat.wwie_users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateUserRequest {
    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;
}
