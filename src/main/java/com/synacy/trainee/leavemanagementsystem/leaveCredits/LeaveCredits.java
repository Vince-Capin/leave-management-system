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
    private Integer totalLeaveCredits;

    @Column(nullable = false)
    private Integer remainingLeaveCredits;

    public void decreaseCredits(int numberOfDays) {
        this.remainingLeaveCredits -= numberOfDays;
    }

    public void increaseCredits(int numberOfDays) {
        this.remainingLeaveCredits += numberOfDays;
    }
}



