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
    public PageResponse<LeaveResponse> fetchPendingLeaveApplications(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "max", defaultValue = "5") int max) {

        Page<LeaveApplication> leave = leaveApplicationService.fetchPendingLeaveApplications(page, max);

        List<LeaveResponse> leaveResponses = leave.getContent().stream()
                .map(LeaveResponse::new)
                .toList();

        int totalLeaves = (int) leave.getTotalElements();


        return new PageResponse<>(totalLeaves, page, leaveResponses);
    }

    @GetMapping("/api/v1/leave/application/history")
    public PageResponse<LeaveResponse> fetchNonLeaveApplicationsByStatus(
            @RequestParam (value = "page", defaultValue = "1") int page,
            @RequestParam (value = "max", defaultValue = "5") int max
    ){
        Page<LeaveApplication> nonPendingLeaves = leaveApplicationService.getNonPendingLeaveApplications(page, max);

        List<LeaveResponse> leaveResponses = nonPendingLeaves.getContent().stream()
                .map(LeaveResponse::new)
                .toList();

        int  totalLeaves = (int) nonPendingLeaves.getTotalElements();

        return new PageResponse<>(totalLeaves, page, leaveResponses);
    }

    @GetMapping("/api/v1/leave/application/{managerId}/active")
    public PageResponse<LeaveResponse> fetchAllPendingLeaveApplicationsByManagerIdAndStatus(
            @PathVariable Long managerId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "max", defaultValue = "5")  int max
    ){
        Page<LeaveApplication> leave = leaveApplicationService.getPendingLeaveApplicationsByManagerIdAndStatus(managerId, page, max);

        List<LeaveResponse> leavesResponses = leave.getContent().stream()
                .map(LeaveResponse::new)
                .toList();

        int totalLeaves = (int) leave.getTotalElements();

        return new PageResponse<>(totalLeaves, page, leavesResponses);
    }

    @GetMapping("/api/v1/leave/application/{managerId}/history")
    public PageResponse<LeaveResponse> fetchAllNonPendingLeaveApplicationsByManagerIdAndStatus(
            @PathVariable Long managerId,
            @RequestParam (value = "page", defaultValue = "1") int page,
            @RequestParam (value = "max", defaultValue = "5") int max
    ){
        Page<LeaveApplication> leaves = leaveApplicationService.fetchNonPendingLeaveApplicationsByManagerId(managerId, page, max);

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
