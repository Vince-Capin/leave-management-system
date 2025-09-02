package com.synacy.trainee.leavemanagementsystem.leaveapplication;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LeaveResponse {
    private final String name;
    private final LeaveType leaveType;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double numberOfDays;
    private final String reason;
    private final LeaveStatus status;

    public LeaveResponse(LeaveApplication leaveApplication) {
        this.name = leaveApplication.getApplicant().getName();
        this.leaveType = leaveApplication.getLeaveType();
        this.startDate = leaveApplication.getStartDate();
        this.endDate = leaveApplication.getEndDate();
        this.numberOfDays = leaveApplication.getNumberOfDays();
        this.reason = leaveApplication.getReason();
        this.status = leaveApplication.getStatus();
    }
}




