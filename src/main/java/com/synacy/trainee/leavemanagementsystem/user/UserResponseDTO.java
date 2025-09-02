package com.synacy.trainee.leavemanagementsystem.user;

import lombok.Getter;

@Getter
public class UserResponseDTO {

    private final Long id;
    private final String name;
    private final UserRole role;
    private final Integer leaveCredits;
    private final String manager;

    public UserResponseDTO(User user, Integer leaveCredits) {
        this.id = user.getId();
        this.name = user.getName();
        this.role = user.getRole();
        this.leaveCredits = leaveCredits;
        this.manager = user.getManager() != null ? user.getManager().getName() : null;
    }
}
