package com.synacy.trainee.leavemanagementsystem.leaveapplication;

import com.synacy.trainee.leavemanagementsystem.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class LeaveRequest {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double numberOfDays;
    private String reason;
}