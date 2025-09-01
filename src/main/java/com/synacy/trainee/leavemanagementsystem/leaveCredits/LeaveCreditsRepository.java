package com.synacy.trainee.leavemanagementsystem.leaveCredits;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveCreditsRepository extends JpaRepository<LeaveCredits, Long> {
}
