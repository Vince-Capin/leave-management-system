package com.synacy.trainee.leavemanagementsystem.leaveCredits;

import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveCreditsNotFoundException;
import com.synacy.trainee.leavemanagementsystem.user.User;
import com.synacy.trainee.leavemanagementsystem.user.UserRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaveCreditsService {

    private final LeaveCreditsRepository leaveCreditsRepository;

    @Autowired
    public LeaveCreditsService(LeaveCreditsRepository leaveCreditsRepository) {
        this.leaveCreditsRepository = leaveCreditsRepository;
    }

    public LeaveCredits setLeaveCreditsForNewUsers (User user, UserRequestDTO userRequest) {
        LeaveCredits leaveCredits = new LeaveCredits();

        leaveCredits.setUser(user);
        leaveCredits.setTotalLeaveCredits(userRequest.leaveCredits());
        leaveCredits.setRemainingLeaveCredits(userRequest.leaveCredits());

        return leaveCreditsRepository.save(leaveCredits);
    }

    public LeaveCredits getLeaveCreditsOfUser(User user) {
        return leaveCreditsRepository.findByUser(user)
                .orElseThrow(() -> new LeaveCreditsNotFoundException(
                                "Leave credits for user %d not found".formatted(user.getId()))
                );
    }
}
