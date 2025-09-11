package com.synacy.trainee.leavemanagementsystem.leaveCredits

import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveApplication
import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveStatus
import spock.lang.Specification

class LeaveCreditsModifierSpec extends Specification {

    private LeaveCreditsRepository leaveCreditsRepository
    private LeaveCreditsModifier leaveCreditsModifier

    def setup() {
        leaveCreditsRepository = Mock(LeaveCreditsRepository)
        leaveCreditsModifier = new LeaveCreditsModifier(leaveCreditsRepository)
    }

    def "modifyLeaveCredits() should decrease leave credits and save when status is PENDING"() {
        given:
        LeaveApplication leaveApplication = Mock(LeaveApplication)
        LeaveCredits leaveCredits = Mock(LeaveCredits)
        LeaveStatus status = LeaveStatus.PENDING

        int numberOfDays = 5
        leaveApplication.getNumberOfDays() >> numberOfDays

        when:
        leaveCreditsModifier.modifyLeaveCredits(leaveApplication, status, leaveCredits)

        then:
        1 * leaveCredits.decreaseCredits(numberOfDays)
        1 * leaveCreditsRepository.save(_ as LeaveCredits)
    }

    def "modifyLeaveCredits() should decrease leave credits and save when status is REJECTED"() {
        given:
        LeaveApplication leaveApplication = Mock(LeaveApplication)
        LeaveCredits leaveCredits = Mock(LeaveCredits)
        LeaveStatus status = LeaveStatus.REJECTED

        int numberOfDays = 5
        leaveApplication.getNumberOfDays() >> numberOfDays

        when:
        leaveCreditsModifier.modifyLeaveCredits(leaveApplication, status, leaveCredits)

        then:
        1 * leaveCredits.increaseCredits(numberOfDays)
        1 * leaveCreditsRepository.save(_ as LeaveCredits)
    }

    def "modifyLeaveCredits() should decrease leave credits and save when status is CANCELLED"() {
        given:
        LeaveApplication leaveApplication = Mock(LeaveApplication)
        LeaveCredits leaveCredits = Mock(LeaveCredits)
        LeaveStatus status = LeaveStatus.CANCELLED

        int numberOfDays = 5
        leaveApplication.getNumberOfDays() >> numberOfDays

        when:
        leaveCreditsModifier.modifyLeaveCredits(leaveApplication, status, leaveCredits)

        then:
        1 * leaveCredits.increaseCredits(numberOfDays)
        1 * leaveCreditsRepository.save(_ as LeaveCredits)
    }

}
