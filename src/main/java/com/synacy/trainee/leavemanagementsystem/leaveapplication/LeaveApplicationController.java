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

    @GetMapping("/api/v1/leave/application/active")
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

    @GetMapping("/api/v1/leave/application/history")
    public PageResponse<LeaveResponse> fetchLeaveApplicationsByStatusNot(
            @RequestParam LeaveStatus status,
            @RequestParam (value = "page", defaultValue = "1") int page,
            @RequestParam (value = "max", defaultValue = "5") int max
    ){
        Page<LeaveApplication> nonPendingLeaves = leaveApplicationService.getLeaveApplicationsByStatusNot(status, page, max);

        List<LeaveResponse> leaveResponses = nonPendingLeaves.getContent().stream()
                .map(LeaveResponse::new)
                .toList();

        int  totalLeaves = (int) nonPendingLeaves.getTotalElements();

        return new PageResponse<>(totalLeaves, page, leaveResponses);
    }

    @GetMapping("/api/v1/leave/application/{managerId}/active")
    public PageResponse<LeaveResponse> fetchLeaveApplicationsByManagerIdAndStatus(
            @PathVariable Long managerId,
            @RequestParam LeaveStatus status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "max", defaultValue = "5")  int max
    ){
        Page<LeaveApplication> leave = leaveApplicationService.getLeaveApplicationsByManagerIdAndStatus(managerId, status, page, max);

        List<LeaveResponse> leavesResponses = leave.getContent().stream()
                .map(LeaveResponse::new)
                .toList();

        int totalLeaves = (int) leave.getTotalElements();

        return new PageResponse<>(totalLeaves, page, leavesResponses);
    }

    @GetMapping("/api/v1/leave/application/{managerId}/history")
    public PageResponse<LeaveResponse> fetchLeaveApplicationsByManagerIdAndStatusNot(
            @PathVariable Long managerId,
            @RequestParam LeaveStatus status,
            @RequestParam (value = "page", defaultValue = "1") int page,
            @RequestParam (value = "max", defaultValue = "5") int max
    ){
        Page<LeaveApplication> leaves = leaveApplicationService.fetchLeaveApplicationsByManagerIdAndStatusNot(managerId, status, page, max);

        List<LeaveResponse> leaveResponses = leaves.getContent().stream()
                .map(LeaveResponse::new)
                .toList();

        int totalLeaves = (int) leaves.getTotalElements();

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
