package com.synacy.trainee.leavemanagementsystem.user;

import lombok.Getter;

@Getter
public class UserManagerResponseDTO {

    private final Long id;
    private final String name;

    public UserManagerResponseDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
    }
}
