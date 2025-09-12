package com.synacy.trainee.leavemanagementsystem.leaveapplication;


import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCredits;
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsModifier;
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsService;
import com.synacy.trainee.leavemanagementsystem.user.User;
import com.synacy.trainee.leavemanagementsystem.user.UserNotFoundException;
import com.synacy.trainee.leavemanagementsystem.user.UserRepository;
import com.synacy.trainee.leavemanagementsystem.web.apierror.InsufficientLeaveCreditsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        if(overlapWithExistingLeaveApplicationRequest(leaveRequest)) {
            throw new LeaveDatesOverlapException("Leave Application already exists");
        }

        User user = userRepository.findById(leaveRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + leaveRequest.getUserId()));

        LeaveApplication leave = new LeaveApplication();
        leave.setApplicant(user);
        leave.setManager(user.getManager());
        leave.setApprover(null);
        leave.setStartDate(leaveRequest.getStartDate());
        leave.setEndDate(leaveRequest.getEndDate());
        leave.setReason(leaveRequest.getReason());
        leave.setNumberOfDays(leaveRequest.getNumberOfDays());
        leave.setStatus(LeaveStatus.PENDING);

        LeaveCredits leaveCredits = leaveCreditsService.getLeaveCreditsOfUser(user);

        if (leaveRequest.getNumberOfDays() > leaveCredits.getRemainingLeaveCredits()) {
            throw new InsufficientLeaveCreditsException(
                    String.format("Insufficient leave credits: requested %d days, but only %d remaining.",
                            leaveRequest.getNumberOfDays(),
                            leaveCredits.getRemainingLeaveCredits())
            );
        }

        leaveCreditsModifier.modifyLeaveCredits(leave, LeaveStatus.PENDING,  leaveCredits);

        return leaveApplicationRepository.save(leave);
    }

    public LeaveApplication updateLeaveStatus(Long leaveId, Long approverId, LeaveStatus status) {
        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave application not found with id: " + leaveId));

        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + approverId));

        leave.setApprover(approver);
        leave.setStatus(status);

        LeaveCredits leaveCredits = leave.getApplicant().getLeaveCredits();

        leaveCreditsModifier.modifyLeaveCredits(leave, status, leaveCredits);

        return leaveApplicationRepository.save(leave);
    }

    public List<LeaveResponse> fetchAllLeaveApplications() {
        List<LeaveApplication> leaveApplications = leaveApplicationRepository.findAll();

        return leaveApplications.stream()
                .map(LeaveResponse::new)
                .toList();
    }

    public Page<LeaveApplication> getLeaveApplicationsByUserId(Long userId, int page, int max) {
        Pageable pageable = PageRequest.of(page - 1, max).withSort(Sort.by(Sort.Direction.DESC, "id"));

        return leaveApplicationRepository.findByApplicant_Id(userId, pageable);
    }

    public Page<LeaveApplication> getLeaveApplicationsByManagerIdAndStatus(Long managerId, LeaveStatus status, int page, int max) {
        Pageable pageable = PageRequest.of(page - 1, max).withSort(Sort.by(Sort.Direction.DESC, "id"));

        return leaveApplicationRepository.findLeaveApplicationByManager_IdAndStatus(managerId, status, pageable);
    }

    public Page<LeaveApplication> fetchLeaveApplicationsByManagerIdAndStatusNot(Long managerId, LeaveStatus status, int page, int max) {
        Pageable pageable = PageRequest.of(page - 1, max).withSort(Sort.by(Sort.Direction.DESC, "id"));

        return leaveApplicationRepository.findByManager_IdAndStatusNot(managerId, status, pageable);
    }

    public Page<LeaveApplication> fetchLeaveApplicationsByStatus(LeaveStatus status, int page, int max) {

        Pageable pageable = PageRequest.of(page - 1, max).withSort(Sort.by(Sort.Direction.DESC, "id"));

        return leaveApplicationRepository.findLeaveApplicationByStatus(status, pageable);
    }

    public Page<LeaveApplication> getLeaveApplicationsByStatusNot(LeaveStatus status, int page, int max) {

        Pageable pageable = PageRequest.of(page - 1, max).withSort(Sort.by(Sort.Direction.DESC, "id"));

        return leaveApplicationRepository.findByStatusNot(status, pageable);
    }

    public List<LeaveApplication> getActiveLeaveApplicationsByManagerId(Long managerId, LeaveStatus status) {
        return leaveApplicationRepository.findByManager_IdAndStatus(managerId, status);
    }

    public void setManagerToNull(List<LeaveApplication> leaveApplications) {
        leaveApplicationRepository.saveAll(leaveApplications);
    }

    public boolean overlapWithExistingLeaveApplicationRequest(LeaveRequest leaveRequest) {
        List<LeaveStatus> blocking = List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED);

        return leaveApplicationRepository.existsOverlapping(
                leaveRequest.getUserId(),
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate(),
                blocking
        );
    }
}

