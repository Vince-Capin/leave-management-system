package com.synacy.trainee.leavemanagementsystem.leaveapplication

import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCredits
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsModifier
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsService
import com.synacy.trainee.leavemanagementsystem.user.User
import com.synacy.trainee.leavemanagementsystem.user.UserNotFoundException
import com.synacy.trainee.leavemanagementsystem.user.UserRepository
import com.synacy.trainee.leavemanagementsystem.user.UserRole
import com.synacy.trainee.leavemanagementsystem.web.apierror.InsufficientLeaveCreditsException
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import spock.lang.Specification
import org.springframework.data.domain.Page
import java.time.LocalDate

class LeaveApplicationServiceSpec extends Specification{

    LeaveApplicationRepository leaveApplicationRepository
    LeaveApplicationService leaveApplicationService
    LeaveCreditsModifier leaveCreditsModifier
    LeaveCreditsService leaveCreditsService
    UserRepository userRepository

    User manager
    User employee1
    User employee2
    LeaveApplication leaveApplication1
    LeaveApplication leaveApplication2
    LeaveApplication leaveApplication3
    LeaveApplication leaveApplication4
    LeaveRequest leaveRequest
    LocalDate appliedDate
    LeaveStatus initialStatus


    def setup () {
        leaveApplicationRepository = Mock(LeaveApplicationRepository)
        userRepository = Mock(UserRepository)
        leaveCreditsModifier = Mock(LeaveCreditsModifier)
        leaveCreditsService = Mock(LeaveCreditsService)

        leaveApplicationService = new LeaveApplicationService(
                leaveApplicationRepository,
                userRepository,
                leaveCreditsModifier,
                leaveCreditsService)

        appliedDate = LocalDate.parse("2025-09-01")
        int numberOfDays = 2
        initialStatus = LeaveStatus.PENDING

        LocalDate startDate = LocalDate.parse("2025-09-02")
        LocalDate endDate = LocalDate.parse("2025-09-03")
        String reason = "Leave Reason"

        manager = new User(id: 1L, name: "Manager", role: UserRole.MANAGER)
        employee1 = new User(id: 2L, name: "Employee1", role: UserRole.EMPLOYEE, manager: manager)
        employee2 = new User(id: 3L, name: "Employee2", role: UserRole.EMPLOYEE, manager: manager)

        leaveRequest = new LeaveRequest(
                userId: employee1.id,
                startDate: startDate,
                endDate: endDate,
                numberOfDays: numberOfDays,
                reason: reason)

        leaveApplication1 = new LeaveApplication(
                id: 1L,
                applicant: employee1,
                manager: manager,
                approver: manager,
                startDate: startDate,
                endDate: endDate,
                appliedDate: appliedDate,
                numberOfDays: numberOfDays,
                reason: reason,
                status: initialStatus )
        leaveApplication2 = new LeaveApplication(
                id: 2L,
                applicant: employee2,
                manager: manager,
                approver: manager,
                startDate: startDate,
                endDate: endDate,
                appliedDate: appliedDate,
                numberOfDays: numberOfDays,
                reason: reason,
                status: initialStatus )
        leaveApplication3 = new LeaveApplication(
                id: 3L,
                applicant: employee1,
                manager: manager,
                approver: manager,
                startDate: startDate,
                endDate: endDate,
                appliedDate: appliedDate,
                numberOfDays: numberOfDays,
                reason: reason,
                status: LeaveStatus.APPROVED )
        leaveApplication4 = new LeaveApplication(
                id: 4L,
                applicant: employee2,
                manager: manager,
                approver: manager,
                startDate: startDate,
                endDate: endDate,
                appliedDate: appliedDate,
                numberOfDays: numberOfDays,
                reason: reason,
                status: LeaveStatus.APPROVED )
    }

    def "createLeaveApplication should create a leave application when user has enough credits"() {
        given:
        def leaveCredits = new LeaveCredits(
                id: 1L,
                user: employee1,
                totalLeaveCredits: 25,
                remainingLeaveCredits: 25
        )

        when:
        def response = leaveApplicationService.createLeaveApplication(leaveRequest)

        then:
        1 * userRepository.findById(employee1.id) >> Optional.of(employee1)
        1 * leaveCreditsService.getLeaveCreditsOfUser(employee1) >> leaveCredits
        1 * leaveCreditsModifier.modifyLeaveCredits(_ as LeaveApplication, LeaveStatus.PENDING, leaveCredits)
        1 * leaveApplicationRepository.save(_ as LeaveApplication) >> { LeaveApplication saved ->
            saved.id = 1L
            return saved
        }

        employee1 == response.applicant
        manager == response.manager
        leaveRequest.reason == response.reason
        initialStatus == response.status
    }

    def "createLeaveApplication should throw a error when user does not exist" () {
        given:
        userRepository.findById(employee1.id) >> Optional.empty()

        when:
        leaveApplicationService.createLeaveApplication(leaveRequest)

        then:
        thrown(UserNotFoundException)
    }

    def "createLeaveApplication should throw a error when number of days applied is greater than remaining leave credits" () {
        given:
        leaveRequest.numberOfDays = 5
        LeaveCredits leaveCredits = new LeaveCredits(remainingLeaveCredits: 3)
        userRepository.findById(employee1.id) >> Optional.of(employee1)
        leaveCreditsService.getLeaveCreditsOfUser(employee1) >> leaveCredits

        when:
        leaveApplicationService.createLeaveApplication(leaveRequest)

        then:
        thrown(InsufficientLeaveCreditsException)
    }

    def "updateLeaveStatus should be able to update the leave status" () {
        given:
        Long leaveId = 1L
        Long approverId = leaveApplication1.approver.id
        LeaveStatus newStatus = LeaveStatus.APPROVED

        def credits = new LeaveCredits(
                id: 1L,
                user: employee1,
                totalLeaveCredits: 25,
                remainingLeaveCredits: 25
        )
        employee1.leaveCredits = credits

        when:
        def response = leaveApplicationService.updateLeaveStatus(leaveId, approverId, newStatus)

        then:
        1 * leaveApplicationRepository.findById(leaveId) >> Optional.of(leaveApplication1)
        1 * userRepository.findById(approverId) >> Optional.of(manager)
        1 * leaveCreditsModifier.modifyLeaveCredits(leaveApplication1, newStatus, credits)
        1 * leaveApplicationRepository.save(leaveApplication1) >> leaveApplication1
        leaveApplication1.status == response.status
    }

    def "updateLeaveStatus should throw a error when leave application is not found!" () {
        given:
        Long leaveId = 1L
        Long approverId = leaveApplication1.approver.id
        LeaveStatus newStatus = LeaveStatus.APPROVED

        when:
        leaveApplicationService.updateLeaveStatus(leaveId, approverId,newStatus)

        then:
        1 * leaveApplicationRepository.findById(leaveId) >> Optional.empty()
        thrown(IllegalArgumentException)
    }

    def "fetchAllLeaveApplications should return all of leave applications" () {
        when:
        def response = leaveApplicationService.fetchAllLeaveApplications()

        then:
        1 * leaveApplicationRepository.findAll() >> [leaveApplication1, leaveApplication2]
        [leaveApplication1.id, leaveApplication2.id] == response*.id
        [leaveApplication1.applicant.name, leaveApplication2.applicant.name] == response*.name
        [leaveApplication1.appliedDate, leaveApplication2.appliedDate] == response*.dateApplied
        [leaveApplication1.startDate, leaveApplication2.startDate] == response*.startDate
        [leaveApplication1.endDate, leaveApplication2.endDate] == response*.endDate
        [(double) leaveApplication1.numberOfDays, leaveApplication2.numberOfDays] == response*.numberOfDays
        [leaveApplication1.reason, leaveApplication2.reason] == response*.reason
        [leaveApplication1.status, leaveApplication2.status] == response*.status
        [leaveApplication1.manager.name, leaveApplication2.manager.name] == response*.manager
    }

    def "getLeaveApplicationsByUserId should return all leave application by the user" (){
        given:
        Long userId = employee1.id
        int page = 1
        int max = 5
        leaveApplication2.applicant = employee1

        Page<LeaveApplication> pageable = Mock()
        pageable.getContent() >> [leaveApplication1, leaveApplication2]

        when:
        def response = leaveApplicationService.getLeaveApplicationsByUserId(userId, page, max)

        then:
        1 * leaveApplicationRepository.findByApplicant_Id(
                userId,
                { Pageable p ->
                    p.pageNumber == 0 &&
                            p.pageSize == max &&
                            p.sort == Sort.by("id").descending()
                }
        ) >> pageable
        [leaveApplication1, leaveApplication2] == response.getContent()
    }

    def "getLeaveApplicationsByManagerIdAndStatus should return a paginated list of leave applications by manager and a pending status" () {
        given:
        Long managerId = 1L
        LeaveStatus status = LeaveStatus.PENDING
        int page = 1
        int max = 5

        Page<LeaveApplication> pageable = Mock()
        pageable.getContent() >> [leaveApplication1, leaveApplication2]

        when:
        def response = leaveApplicationService.getLeaveApplicationsByManagerIdAndStatus(managerId, status, page, max)

        then:
        1 * leaveApplicationRepository.findLeaveApplicationByManager_IdAndStatus(
                managerId,
                status,
                { Pageable p ->
                    p.pageNumber == 0 &&
                            p.pageSize == max &&
                            p.sort == Sort.by("id").descending()
                }
        ) >> pageable
        [leaveApplication1, leaveApplication2] == response.getContent()
    }

    def "fetchLeaveApplicationsByManagerIdAndStatusNot should return a paginated list of leave applications by manager and NOT the status given" () {
        given:
        Long managerId = 1L
        LeaveStatus status = LeaveStatus.PENDING
        int page = 1
        int max = 5

        Page<LeaveApplication> pageable = Mock()
        pageable.getContent() >> [leaveApplication3, leaveApplication4]

        when:
        def response = leaveApplicationService.fetchLeaveApplicationsByManagerIdAndStatusNot(managerId, status, page, max)

        then:
        1 * leaveApplicationRepository.findByManager_IdAndStatusNot(
                managerId,
                status,
                { Pageable p ->
                    p.pageNumber == 0 &&
                            p.pageSize == max &&
                            p.sort == Sort.by("id").descending()
                }
        ) >> pageable
        [leaveApplication3, leaveApplication4] == response.getContent()
        status != response.getContent().status

    }

    def "fetchLeaveApplicationsByStatus should return a paginated list of leave application by status" (){
        given:
        LeaveStatus status = LeaveStatus.PENDING
        int page = 1
        int max = 5

        Page<LeaveApplication> pageable = Mock()
        pageable.getContent() >> [leaveApplication1, leaveApplication2]

        when:
        def response = leaveApplicationService.fetchLeaveApplicationsByStatus(status, page, max)

        then:
        1 * leaveApplicationRepository.findLeaveApplicationByStatus(
                status,
                { Pageable p ->
                    p.pageNumber == 0 &&
                            p.pageSize == max &&
                            p.sort == Sort.by("id").descending()
                }
        ) >> pageable
        [leaveApplication1, leaveApplication2] == response.getContent()
    }

    def "getLeaveApplicationsByStatusNot should return a paginated list of leave applications but not with the status given" () {
        given:
        LeaveStatus status = LeaveStatus.PENDING
        int page = 1
        int max = 5

        Page<LeaveApplication> pageable = Mock()
        pageable.getContent() >> [leaveApplication3, leaveApplication4]

        when:
        def response = leaveApplicationService.getLeaveApplicationsByStatusNot(status, page, max)

        then:
        1 * leaveApplicationRepository.findByStatusNot(
                status,
                { Pageable p ->
                    p.pageNumber == 0 && p.pageSize == max && p.sort == Sort.by("id").descending()
                }
        ) >> pageable
        [leaveApplication3, leaveApplication4] == response.getContent()
        status != response.getContent().status
    }


    def "getActiveLeaveApplicationsByManagerId should return a list of active(pending) leave applications by manager" (){
        given:
        Long managerId = manager.id
        LeaveStatus status = LeaveStatus.PENDING

        when:
        def response = leaveApplicationService.getActiveLeaveApplicationsByManagerId(managerId, status)

        then:
        1 * leaveApplicationRepository.findByManager_IdAndStatus(managerId, status) >> [leaveApplication1, leaveApplication2]
        [leaveApplication1, leaveApplication2] == response
    }

    def "setManagerToNull should be able to null the managers of employee" () {
        given:
        List<LeaveApplication> leaveApplications = [leaveApplication1, leaveApplication2]

        when:
        leaveApplicationService.setManagerToNull(leaveApplications)

        then:
        1 * leaveApplicationRepository.saveAll {leaveApplications}
    }
}
