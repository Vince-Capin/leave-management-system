package com.synacy.trainee.leavemanagementsystem.leaveapplication;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByApplicant_Id(Long id);

    Page<LeaveApplication> findLeaveApplicationByManager_Id(Long managerId, Pageable pageable);

    Page<LeaveApplication> findLeaveApplicationByStatus(LeaveStatus status, Pageable pageable);
}
