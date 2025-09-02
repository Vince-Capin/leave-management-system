package com.synacy.trainee.leavemanagementsystem.user;

import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCredits;
import lombok.Getter;

@Getter
public class UserResponseDTO {

    private final Long id;
    private final String name;
    private final UserRole role;
    private final Integer vacationLeaveCredits;
    private final Integer sickLeaveCredits;
    private final Integer emergencyLeaveCredits;
    private final String manager;

    public UserResponseDTO(User user, LeaveCredits leaveCredits) {
        this.id = user.getId();
        this.name = user.getName();
        this.role = user.getRole();
        this.vacationLeaveCredits = leaveCredits.getVacationLeaveCredits();
        this.sickLeaveCredits = leaveCredits.getSickLeaveCredits();
        this.emergencyLeaveCredits = leaveCredits.getEmergencyLeaveCredits();
        this.manager = user.getManager() != null ? user.getManager().getName() : null;
    }
}
