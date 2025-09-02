package com.synacy.trainee.leavemanagementsystem.leaveCredits;

import com.synacy.trainee.leavemanagementsystem.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveCreditsRepository extends JpaRepository<LeaveCredits, Long> {
    Optional<LeaveCredits> findByUser(User id);
}
