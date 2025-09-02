package com.synacy.trainee.leavemanagementsystem.leaveCredits;

import com.synacy.trainee.leavemanagementsystem.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LeaveCredits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Integer vacationLeaveCredits;

    @Column(nullable = false)
    private Integer sickLeaveCredits;

    @Column(nullable = false)
    private Integer emergencyLeaveCredits;

    @Transient
    public int getTotalLeaveCredits() {
        return vacationLeaveCredits + sickLeaveCredits + emergencyLeaveCredits;
    }
}
