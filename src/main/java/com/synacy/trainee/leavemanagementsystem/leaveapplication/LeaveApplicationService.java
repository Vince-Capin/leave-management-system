package com.synacy.trainee.leavemanagementsystem.leaveapplication;


import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCredits;
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsModifier;
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsService;
import com.synacy.trainee.leavemanagementsystem.user.User;
import com.synacy.trainee.leavemanagementsystem.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeaveApplicationService {
    private final UserRepository userRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveCreditsModifier leaveCreditsModifier;
    private final LeaveCreditsService leaveCreditsService;

    @Autowired
    public LeaveApplicationService (LeaveApplicationRepository leaveApplicationRepository,
                                    UserRepository userRepository,
                                    LeaveCreditsModifier leaveCreditsModifier,
                                    LeaveCreditsService leaveCreditsService) {
        this.leaveCreditsService = leaveCreditsService;
        this.leaveCreditsModifier = leaveCreditsModifier;
        this.userRepository = userRepository;
        this.leaveApplicationRepository = leaveApplicationRepository;
    }

    @Transactional
    public LeaveApplication createLeaveApplication(LeaveRequest leaveRequest) {
        User user = userRepository.findById(leaveRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + leaveRequest.getUserId()));

        LeaveApplication leave = new LeaveApplication();
        leave.setApplicant(user);
        leave.setManager(user.getManager());
        leave.setStartDate(leaveRequest.getStartDate());
        leave.setEndDate(leaveRequest.getEndDate());
        leave.setReason(leaveRequest.getReason());
        leave.setNumberOfDays(leaveRequest.getNumberOfDays());
        leave.setStatus(LeaveStatus.PENDING);

        return leaveApplicationRepository.save(leave);
    }

    public LeaveApplication updateLeaveStatus(Long leaveId, LeaveStatus status) {
        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave application not found with id: " + leaveId));

        LeaveStatus previousStatus = leave.getStatus();
        leave.setStatus(status);

        if (!previousStatus.equals(status)) {
            LeaveCredits leaveCredits = leaveCreditsService.getLeaveCreditsOfUser(leave.getApplicant());
            leaveCreditsModifier.modifyLeaveCredits(leave, status, leaveCredits);
        }

        return leaveApplicationRepository.save(leave);
    }

    public List<LeaveResponse> fetchAllLeaveApplications() {
        List<LeaveApplication> leaveApplications = leaveApplicationRepository.findAll();

        return leaveApplications.stream()
                .map(LeaveResponse::new)
                .toList();
    }

    public List<LeaveApplication> getLeaveApplicationsByUserId(Long userId) {
        return leaveApplicationRepository.findByApplicant_Id(userId);
    }

    public List<LeaveApplication> getLeaveApplicationsByManagerId(Long managerId) {
        return leaveApplicationRepository.findLeaveApplicationByManager_Id(managerId);
    }
}
