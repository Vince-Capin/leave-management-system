package com.synacy.trainee.leavemanagementsystem.leaveapplication

import com.synacy.trainee.leavemanagementsystem.user.User
import com.synacy.trainee.leavemanagementsystem.user.UserRole
import com.synacy.trainee.leavemanagementsystem.web.PageResponse
import org.springframework.data.domain.Page
import spock.lang.Specification

import javax.swing.text.AbstractDocument
import java.time.LocalDate

class LeaveApplicationControllerSpec extends Specification {

    LeaveApplicationController leaveApplicationController
    LeaveApplicationService leaveApplicationService

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

    def setup() {
        leaveApplicationService = Mock(LeaveApplicationService)
        leaveApplicationController = new LeaveApplicationController(leaveApplicationService)

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
                startDate: startDate,
                endDate: endDate,
                appliedDate: appliedDate,
                numberOfDays: numberOfDays,
                reason: reason,
                status: LeaveStatus.APPROVED )
    }

    def "createLeaveApplication should create a leave application and return the correct response"() {
        given:
        1 * leaveApplicationService.createLeaveApplication(leaveRequest) >> leaveApplication1

        when:
        def response = leaveApplicationController.createLeaveApplication(leaveRequest)

        then:
        1L == response.id
        employee1.name == response.name
        appliedDate == response.dateApplied
        leaveRequest.startDate == response.startDate
        leaveRequest.endDate == response.endDate
        leaveRequest.numberOfDays == response.numberOfDays
        leaveRequest.reason == response.reason
        initialStatus == response.status
        manager.name == response.manager
    }

    def "fetchPendingLeaveApplications should return a paginated list of leave applications with pending statuses"(){
        given:
        int page = 1
        int max = 5
        int totalCount = 2
        int totalPages = 1

        Page<LeaveApplication> leaveApplicationPage = Mock()
        leaveApplicationPage.getContent() >> [leaveApplication1, leaveApplication2]
        leaveApplicationPage.getTotalElements() >> totalCount
        leaveApplicationPage.totalPages >> totalPages

        when:
        PageResponse<LeaveResponse> response = leaveApplicationController.fetchLeaveApplicationsByStatus(initialStatus, page, max)

        then:
        1 * leaveApplicationService.fetchLeaveApplicationsByStatus(initialStatus, page, max) >> leaveApplicationPage
        page == response.pageNumber()
        totalCount == response.totalCount()
        [leaveApplication1.id, leaveApplication2.id] == response.content()*.id
        [leaveApplication1.applicant.name, leaveApplication2.applicant.name] == response.content()*.name
        [leaveApplication1.appliedDate, leaveApplication2.appliedDate] == response.content()*.dateApplied
        [leaveApplication1.startDate, leaveApplication2.startDate] == response.content()*.startDate
        [leaveApplication1.endDate, leaveApplication2.endDate] == response.content()*.endDate
        [(double) leaveApplication1.numberOfDays, leaveApplication2.numberOfDays] == response.content()*.numberOfDays
        [leaveApplication1.reason, leaveApplication2.reason] == response.content()*.reason
        [leaveApplication1.status, leaveApplication2.status] == response.content()*.status
        [leaveApplication1.manager.name, leaveApplication2.manager.name] == response.content()*.manager
    }

    def "fetchLeaveApplicationsByStatus should return an empty page when no leave applications match"() {
        given:
        int page = 1
        int max = 5
        int totalCount = 0
        int totalPages = 0

        Page<LeaveApplication> leaveApplicationPage = Mock()
        leaveApplicationPage.getContent() >> []
        leaveApplicationPage.getTotalElements() >> totalCount
        leaveApplicationPage.totalPages >> totalPages


        when:
        PageResponse<LeaveResponse> response =
                leaveApplicationController.fetchLeaveApplicationsByStatus(initialStatus, page, max)

        then:
        1 * leaveApplicationService.fetchLeaveApplicationsByStatus(initialStatus, page, max) >> leaveApplicationPage

        page == response.pageNumber()
        totalCount == response.totalCount()
        [] == response.content()
    }

    def "fetchLeaveApplicationByStatusNot should return a paginated list of leave applications with status that is not pending"() {
        given:
        int page = 1
        int max = 5
        int totalCount = 2
        int totalPages = 1

        Page<LeaveApplication> leaveApplicationPage = Mock()
        leaveApplicationPage.getContent() >> [leaveApplication3, leaveApplication4]
        leaveApplicationPage.getTotalElements() >> totalCount
        leaveApplicationPage.totalPages >> totalPages

        when:
        PageResponse<LeaveResponse> response = leaveApplicationController.fetchLeaveApplicationsByStatusNot(initialStatus, page, max)

        then:
        1 * leaveApplicationService.getLeaveApplicationsByStatusNot(initialStatus, page, max) >> leaveApplicationPage

        page == response.pageNumber()
        totalCount == response.totalCount()
        [leaveApplication3.id, leaveApplication4.id] == response.content()*.id
        [leaveApplication3.applicant.name, leaveApplication4.applicant.name] == response.content()*.name
        [leaveApplication3.appliedDate, leaveApplication4.appliedDate] == response.content()*.dateApplied
        [leaveApplication3.startDate, leaveApplication4.startDate] == response.content()*.startDate
        [leaveApplication3.endDate, leaveApplication4.endDate] == response.content()*.endDate
        [(double) leaveApplication3.numberOfDays, leaveApplication4.numberOfDays] == response.content()*.numberOfDays
        [leaveApplication3.reason, leaveApplication4.reason] == response.content()*.reason
        [leaveApplication3.status, leaveApplication4.status] == response.content()*.status
        [leaveApplication3.manager.name, leaveApplication4.manager.name] == response.content()*.manager
    }

    def "fetchLeaveApplicationByStatusNot should return a an empty page when no leave applications match"() {
        given:
        int page = 1
        int max = 5
        int totalCount = 0
        int totalPages = 0

        Page<LeaveApplication> leaveApplicationPage = Mock()
        leaveApplicationPage.getContent() >> []
        leaveApplicationPage.getTotalElements() >> totalCount
        leaveApplicationPage.totalPages >> totalPages

        when:
        PageResponse<LeaveResponse> response =
                leaveApplicationController.fetchLeaveApplicationsByStatusNot(initialStatus, page, max)

        then:
        1 * leaveApplicationService.getLeaveApplicationsByStatusNot(initialStatus, page, max) >> leaveApplicationPage
        page == response.pageNumber()
        totalCount == response.totalCount()
        [] == response.content()
    }

    def "fetchLeaveApplicationByManagerIdAndStatus should a return a paginated list of leave applications by manager"(){
        given:
        int page = 1
        int max = 5
        int totalCount = 2
        int totalPages = 1
        Long managerId = manager.id
        LeaveStatus status = LeaveStatus.APPROVED

        Page<LeaveApplication> leaveApplicationPage = Mock()
        leaveApplicationPage.getContent() >> [leaveApplication3, leaveApplication4]
        leaveApplicationPage.getTotalElements() >> totalCount
        leaveApplicationPage.totalPages >> totalPages

        when:
        PageResponse<LeaveResponse> response = leaveApplicationController.fetchLeaveApplicationsByManagerIdAndStatus(managerId, status, page, max)

        then:
        1 * leaveApplicationService.getLeaveApplicationsByManagerIdAndStatus(managerId, status, page, max) >> leaveApplicationPage
        page == response.pageNumber()
        totalCount == response.totalCount()
        [leaveApplication3.id, leaveApplication4.id] == response.content()*.id
        [leaveApplication3.applicant.name, leaveApplication4.applicant.name] == response.content()*.name
        [leaveApplication3.appliedDate, leaveApplication4.appliedDate] == response.content()*.dateApplied
        [leaveApplication3.startDate, leaveApplication4.startDate] == response.content()*.startDate
        [leaveApplication3.endDate, leaveApplication4.endDate] == response.content()*.endDate
        [(double) leaveApplication3.numberOfDays, leaveApplication4.numberOfDays] == response.content()*.numberOfDays
        [leaveApplication3.reason, leaveApplication4.reason] == response.content()*.reason
        [leaveApplication3.status, leaveApplication4.status] == response.content()*.status
        [leaveApplication3.manager.name, leaveApplication4.manager.name] == response.content()*.manager
    }

    def "fetchLeaveApplicationByManagerIdAndStatus should a return a empty list of leave applications by manager"(){
        given:
        int page = 1
        int max = 5
        int totalCount = 0
        int totalPages = 0

        Long managerId = manager.id
        LeaveStatus status = LeaveStatus.APPROVED

        Page<LeaveApplication> leaveApplicationPage = Mock()
        leaveApplicationPage.getContent() >> []
        leaveApplicationPage.getTotalElements() >> totalCount
        leaveApplicationPage.totalPages >> totalPages

        when:
        PageResponse<LeaveResponse> response = leaveApplicationController.fetchLeaveApplicationsByManagerIdAndStatus(managerId, status, page, max)

        then:
        1 * leaveApplicationService.getLeaveApplicationsByManagerIdAndStatus(managerId, status, page, max) >> leaveApplicationPage
        page == response.pageNumber()
        totalCount == response.totalCount()
        [] == response.content()
    }

    def "updateLeaveApplicationStatus should be able to update the status of the leave application"() {
        given:
        Long id = 3L
        Long approverId = leaveApplication1.approver.id
        LeaveStatus newStatus = LeaveStatus.APPROVED
        leaveApplication1.status = newStatus

        when:
        LeaveResponse updatedLeave = leaveApplicationController.updateLeaveApplicationStatus(id, approverId, newStatus)

        then:
        1 * leaveApplicationService.updateLeaveStatus(id, approverId, newStatus) >> leaveApplication1
        newStatus == updatedLeave.status
    }

    def "getUserLeaveApplication should be able to get all leave applications of the user" () {
        given:
        Long userId = employee1.id
        int page = 1
        int max = 5
        int totalCount = 2
        int totalPages = 1
        leaveApplication2.applicant = employee1

        Page<LeaveApplication> leaveApplicationPage = Mock()
        leaveApplicationPage.getContent() >> [leaveApplication1, leaveApplication2]
        leaveApplicationPage.getTotalElements() >> totalCount
        leaveApplicationPage.totalPages >> totalPages

        when:
        PageResponse<LeaveResponse> response = leaveApplicationController.getUserLeaveApplications(userId, page, max)

        then:
        1 * leaveApplicationService.getLeaveApplicationsByUserId(userId, page, max) >> leaveApplicationPage
        [leaveApplication1.id, leaveApplication2.id] == response.content()*.id
        [leaveApplication1.applicant.name, leaveApplication2.applicant.name] == response.content()*.name
        [leaveApplication1.appliedDate, leaveApplication2.appliedDate] == response.content()*.dateApplied
        [leaveApplication1.startDate, leaveApplication2.startDate] == response.content()*.startDate
        [leaveApplication1.endDate, leaveApplication2.endDate] == response.content()*.endDate
        [(double) leaveApplication1.numberOfDays, leaveApplication2.numberOfDays] == response.content()*.numberOfDays
        [leaveApplication1.reason, leaveApplication2.reason] == response.content()*.reason
        [leaveApplication1.status, leaveApplication2.status] == response.content()*.status
        [leaveApplication1.manager.name, leaveApplication2.manager.name] == response.content()*.manager
    }
}