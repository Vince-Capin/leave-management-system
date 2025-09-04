package com.synacy.trainee.leavemanagementsystem.leaveapplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<LeaveResponse> fetchAllLeaveApplications() {
        return leaveApplicationService.fetchAllLeaveApplications();
    }

    @PutMapping("/api/v1/leave/application/{id}/status")
    public LeaveResponse updateLeaveApplicationStatus(@PathVariable Long id, @RequestParam LeaveStatus leaveStatus) {
        LeaveApplication updateLeave = leaveApplicationService.updateLeaveStatus(id, leaveStatus);
        return new LeaveResponse(updateLeave);
    }

    @GetMapping("/api/v1/users/{userId}/leave-applications")
    public List<LeaveResponse> getUserLeaveApplications(@PathVariable Long userId) {
        return leaveApplicationService.getLeaveApplicationsByUserId(userId).stream()
                .map(LeaveResponse::new)
                .toList();
    }
}
