package com.synacy.trainee.leavemanagementsystem.user;

import org.antlr.v4.runtime.misc.NotNull;

public record UserRequestDTO(
        @NotNull String name,
        @NotNull UserRole role,
        Integer leaveCredits,
        Long managerId
        ) {
}
