package com.synacy.trainee.leavemanagementsystem.user;

import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCredits;
import lombok.Getter;

@Getter
public class UserResponseDTO {

    private final Long id;
    private final String name;
    private final UserRole role;
    private final Integer totalLeaveCredits;
    private final Integer remainingLeaveCredits;
    private final String manager;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.role = user.getRole();
        this.totalLeaveCredits = user.getLeaveCredits() != null ? user.getLeaveCredits().getTotalLeaveCredits() : null;
        this.remainingLeaveCredits = user.getLeaveCredits() != null ? user.getLeaveCredits().getTotalLeaveCredits() : null;
        this.manager = user.getManager() != null ? user.getManager().getName() : null;
    }
}
