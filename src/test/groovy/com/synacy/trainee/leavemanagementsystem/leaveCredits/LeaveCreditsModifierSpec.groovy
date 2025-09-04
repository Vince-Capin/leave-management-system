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

    def "modifyLeaveCredits() should decrease leave credits and save when status is APPROVED"() {
        given:
        LeaveApplication leaveApplication = Mock(LeaveApplication)
        LeaveCredits leaveCredits = Mock(LeaveCredits)
        def numberOfDays = 5

        leaveApplication.getNumberOfDays() >> numberOfDays

        when:
        leaveCreditsModifier.modifyLeaveCredits(leaveApplication, LeaveStatus.APPROVED, leaveCredits)

        then:
        1 * leaveCredits.decreaseCredits(numberOfDays)
        1 * leaveCreditsRepository.save(leaveCredits)
    }

}
