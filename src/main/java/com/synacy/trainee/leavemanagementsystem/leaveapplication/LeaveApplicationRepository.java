package com.synacy.trainee.leavemanagementsystem.leaveapplication;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    Page<LeaveApplication> findByApplicant_Id(Long id, Pageable pageable);

    Page<LeaveApplication> findLeaveApplicationByManager_IdAndStatus(Long managerId, LeaveStatus status, Pageable pageable);

    Page<LeaveApplication> findLeaveApplicationByStatus(LeaveStatus status, Pageable pageable);

    Page<LeaveApplication> findByStatusNot(LeaveStatus status, Pageable pageable);

    Page<LeaveApplication> findByManager_IdAndStatusNot(Long managerId, LeaveStatus status, Pageable pageable);

    List<LeaveApplication> findByManager_IdAndStatus(Long managerId, LeaveStatus status);

    @Query("""
      select (count(la) > 0) from LeaveApplication la
      where la.applicant.id = :applicantId
        and la.status in :blockingStatuses
        and la.startDate <= :endDate
        and la.endDate   >= :startDate
    """)
    boolean existsOverlapping(@Param("applicantId") Long applicantId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("blockingStatuses") List<LeaveStatus> blockingStatuses);
}
