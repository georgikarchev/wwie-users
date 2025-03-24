package com.whatwillieat.wwie_users.dto;

import com.whatwillieat.wwie_users.model.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateRoleRequest {
    @NotNull
    private UserRole userRole;
}

