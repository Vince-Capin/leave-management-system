package com.synacy.trainee.leavemanagementsystem.leaveapplication;


import com.synacy.trainee.leavemanagementsystem.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "leave_applications")
public class LeaveApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_application_sequence")
    @SequenceGenerator(name = "leave_application_sequence", sequenceName = "leave_application_sequence", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User applicant;

    @Column
    private LocalDate startDate;
    @Column
    private LocalDate endDate;
    @Column
    private LocalDate appliedDate = LocalDate.now();
    @Column
    private double numberOfDays;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Column
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;
}

