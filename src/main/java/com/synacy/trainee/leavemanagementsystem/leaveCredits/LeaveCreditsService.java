package com.synacy.trainee.leavemanagementsystem.leaveCredits;

import com.synacy.trainee.leavemanagementsystem.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaveCreditsService {

    private final LeaveCreditsRepository leaveCreditsRepository;

    @Autowired
    public LeaveCreditsService(LeaveCreditsRepository leaveCreditsRepository) {
        this.leaveCreditsRepository = leaveCreditsRepository;
    }

    public void setLeaveCreditsForNewUsers (User user, Integer leaveCreditsNumber){
        LeaveCredits leaveCredits = new LeaveCredits();

        leaveCredits.setUser(user);
        leaveCredits.setTotalLeaveCredits(leaveCreditsNumber);
        leaveCredits.setRemainingLeaveCredits(leaveCreditsNumber);

        leaveCreditsRepository.save(leaveCredits);
    }
}
