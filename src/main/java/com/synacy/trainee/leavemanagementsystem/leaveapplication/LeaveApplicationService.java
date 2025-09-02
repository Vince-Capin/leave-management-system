package com.synacy.trainee.leavemanagementsystem.leaveapplication;


import com.synacy.trainee.leavemanagementsystem.user.User;
import com.synacy.trainee.leavemanagementsystem.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveApplicationService {
    private final UserRepository userRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    public LeaveApplicationService (LeaveApplicationRepository leaveApplicationRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.leaveApplicationRepository = leaveApplicationRepository;
    }

    public LeaveApplication createLeaveApplication(LeaveRequest leaveRequest) {

        User user = userRepository.findById(leaveRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + leaveRequest.getUserId()));

        LeaveApplication leave = new LeaveApplication();

        leave.setApplicant(user);
        leave.setLeaveType(leaveRequest.getLeaveType());
        leave.setStartDate(leaveRequest.getStartDate());
        leave.setEndDate(leaveRequest.getEndDate());
        leave.setReason(leaveRequest.getReason());
        leave.setNumberOfDays(leaveRequest.getNumberOfDays());
        leave.setStatus(LeaveStatus.PENDING);

        return leaveApplicationRepository.save(leave);
    }

    public LeaveApplication fetchAllLeaveApplications() {
        List<LeaveApplication> leaveApplications = leaveApplicationRepository.findAll();
        return (LeaveApplication) leaveApplications;
    }
}
