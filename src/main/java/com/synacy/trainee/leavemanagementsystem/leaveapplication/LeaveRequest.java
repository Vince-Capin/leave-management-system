package com.synacy.trainee.leavemanagementsystem.leaveapplication;


import com.synacy.trainee.leavemanagementsystem.user.User;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LeaveRequest {
    private Long userId;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private double numberOfDays;
    private String reason;
}