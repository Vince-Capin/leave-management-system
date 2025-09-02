package com.synacy.trainee.leavemanagementsystem.leaveCredits;

import com.synacy.trainee.leavemanagementsystem.user.User;
import com.synacy.trainee.leavemanagementsystem.user.UserRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LeaveCreditsService {

    private final LeaveCreditsRepository leaveCreditsRepository;

    @Autowired
    public LeaveCreditsService(LeaveCreditsRepository leaveCreditsRepository) {
        this.leaveCreditsRepository = leaveCreditsRepository;
    }

    public void setLeaveCreditsForNewUsers (User user, UserRequestDTO userRequest) {
        LeaveCredits leaveCredits = new LeaveCredits();

        leaveCredits.setUser(user);
        leaveCredits.setVacationLeaveCredits(userRequest.vacationLeaveCredits());
        leaveCredits.setSickLeaveCredits(userRequest.sickLeaveCredits());
        leaveCredits.setEmergencyLeaveCredits(userRequest.emergencyLeaveCredits());

        leaveCreditsRepository.save(leaveCredits);
    }

    public Optional<LeaveCredits> getLeaveCreditsOfUsers (User user) {
        return leaveCreditsRepository.findByUser(user);
    }
}
