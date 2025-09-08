package com.synacy.trainee.leavemanagementsystem.leaveapplication;

import com.synacy.trainee.leavemanagementsystem.web.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @GetMapping("/api/v1/leave/application/admin/history")
    public PageResponse<LeaveResponse> fetchAllNonPendingLeaveApplications(
            @RequestParam (value = "page" , defaultValue = "1") int page,
            @RequestParam (value = "max", defaultValue = "5") int max) {

        Page<LeaveApplication> leave = leaveApplicationService.fetchAllNonPendingLeaveApplications(page, max);

        List<LeaveResponse> leaveResponses = leave.getContent().stream()
                .map(LeaveResponse::new)
                .toList();

        int totalLeaves = (int) leave.getTotalElements();

        return new PageResponse<>(totalLeaves, page, leaveResponses);
    }

    @GetMapping("/api/v1/leave/application/status")
    public PageResponse<LeaveResponse> fetchLeaveApplicationsByStatus(
            @RequestParam LeaveStatus status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "max", defaultValue = "5") int max) {

        Page<LeaveApplication> leave = leaveApplicationService.fetchLeaveApplicationsByStatus(status, page, max);

        List<LeaveResponse> leaveResponses = leave.getContent().stream()
                .map(LeaveResponse::new)
                .toList();

        int totalLeaves = (int) leave.getTotalElements();


        return new PageResponse<>(totalLeaves, page, leaveResponses);
    }

    @GetMapping("api/v1/leave/applications/manager/{id}")
    public PageResponse<LeaveResponse> getLeaveApplicationsByManagerId(
            @PathVariable Long id,
            @RequestParam LeaveStatus status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "max", defaultValue = "5") int max) {
        Page<LeaveApplication> leave = leaveApplicationService.getLeaveApplicationsByManagerIdAndStatus(id, status, page, max);

        List<LeaveResponse> leaveResponses = leave.getContent().stream()
                .map(LeaveResponse::new)
                .toList();
        int totalLeaves = (int) leave.getTotalElements();

        return new PageResponse<>(totalLeaves, page, leaveResponses);
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
