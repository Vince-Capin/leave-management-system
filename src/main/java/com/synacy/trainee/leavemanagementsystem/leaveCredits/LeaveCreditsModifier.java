package com.synacy.trainee.leavemanagementsystem.leaveCredits;

import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveApplication;
import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveStatus;
import org.springframework.stereotype.Service;


@Service
public class LeaveCreditsModifier {
    private final LeaveCreditsRepository leaveCreditsRepository;

    public LeaveCreditsModifier(LeaveCreditsRepository leaveCreditsRepository) {
        this.leaveCreditsRepository = leaveCreditsRepository;
    }

    public void modifyLeaveCredits(LeaveApplication leaveApplication, LeaveStatus status, LeaveCredits leaveCredits) {
        int numberOfDays = leaveApplication.getNumberOfDays();

        if (status == LeaveStatus.PENDING) {
            leaveCredits.decreaseCredits(numberOfDays);
        } else if (status == LeaveStatus.REJECTED || status == LeaveStatus.CANCELLED) {
            leaveCredits.increaseCredits(numberOfDays);
        }

        leaveCreditsRepository.save(leaveCredits);
    }
}
