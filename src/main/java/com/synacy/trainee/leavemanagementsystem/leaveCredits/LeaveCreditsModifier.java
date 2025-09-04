package com.synacy.trainee.leavemanagementsystem.leaveCredits;

import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveApplication;
import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveApplicationRepository;
import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveStatus;
import org.springframework.stereotype.Service;


@Service
public class LeaveCreditsModifier {
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveCreditsRepository leaveCreditsRepository;

    public LeaveCreditsModifier(LeaveApplicationRepository leaveApplicationRepository,
                                LeaveCreditsRepository leaveCreditsRepository) {
        this.leaveApplicationRepository = leaveApplicationRepository;
        this.leaveCreditsRepository = leaveCreditsRepository;
    }

    public void modifyLeaveCredits(LeaveApplication leaveApplication, LeaveStatus status, LeaveCredits leaveCredits) {
        int numberOfDays = leaveApplication.getNumberOfDays();

        if (status == LeaveStatus.APPROVED) leaveCredits.decreaseCredits(numberOfDays);

        leaveCreditsRepository.save(leaveCredits);
    }
}
