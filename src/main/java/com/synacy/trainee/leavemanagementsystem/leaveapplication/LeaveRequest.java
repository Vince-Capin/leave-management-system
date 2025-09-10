package com.synacy.trainee.leavemanagementsystem.leaveapplication;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class LeaveRequest {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numberOfDays;
    private String reason;
}