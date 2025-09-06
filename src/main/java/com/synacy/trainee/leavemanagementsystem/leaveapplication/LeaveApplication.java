package com.synacy.trainee.leavemanagementsystem.leaveapplication;


import com.synacy.trainee.leavemanagementsystem.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDate appliedDate;
    private Integer numberOfDays;

    @Column
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;
}

