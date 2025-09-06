package com.synacy.trainee.leavemanagementsystem.leaveapplication;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LeaveResponse {
    private final Long id;
    private final String name;
    private final LocalDate dateApplied;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double numberOfDays;
    private final String reason;
    private final LeaveStatus status;
    private final String manager;

    public LeaveResponse(LeaveApplication leaveApplication) {
        this.id = leaveApplication.getId();
        this.name = leaveApplication.getApplicant().getName();
        this.dateApplied = leaveApplication.getAppliedDate();
        this.startDate = leaveApplication.getStartDate();
        this.endDate = leaveApplication.getEndDate();
        this.numberOfDays = leaveApplication.getNumberOfDays();
        this.reason = leaveApplication.getReason();
        this.status = leaveApplication.getStatus();
        this.manager = leaveApplication.getManager() != null ? leaveApplication.getManager().getName() : null;
    }
}




