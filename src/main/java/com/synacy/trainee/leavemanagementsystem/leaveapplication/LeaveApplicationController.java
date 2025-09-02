package com.synacy.trainee.leavemanagementsystem.leaveapplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LeaveApplicationController {

    private final LeaveApplicationService leaveApplicationService;

    @Autowired
    public LeaveApplicationController(LeaveApplicationService leaveApplicationService) {
     this.leaveApplicationService = leaveApplicationService;
    }

    @PostMapping("/api/v1/leave/application")
    public LeaveResponse createLeaveApplication(@RequestBody LeaveRequest leaveRequest) {
        LeaveApplication leave = leaveApplicationService.createLeaveApplication(leaveRequest);
        return new LeaveResponse(leave);
    }

    @GetMapping("/api/v1/leave/application")
    public LeaveApplication fetchAllLeaveApplications() {
        return leaveApplicationService.fetchAllLeaveApplications();
    }
}
