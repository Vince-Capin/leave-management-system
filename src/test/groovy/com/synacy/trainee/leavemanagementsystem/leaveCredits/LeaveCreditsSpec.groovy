package com.synacy.trainee.leavemanagementsystem.leaveCredits

import spock.lang.Specification

class LeaveCreditsSpec extends Specification {

    def "decreaseCredits() should decrease the credits by the specified number of days"() {
        given:
        LeaveCredits leaveCredits = new LeaveCredits(remainingLeaveCredits: 20)
        def numberOfDays = 5

        when:
        leaveCredits.decreaseCredits(numberOfDays)

        then:
        leaveCredits.remainingLeaveCredits == 15;
    }

    def "increaseCredits() should increase the credits by the specified number of days" () {
        given:
        LeaveCredits leaveCredits = new LeaveCredits(remainingLeaveCredits: 20)
        def numberOfDays = 5

        when:
        leaveCredits.increaseCredits(numberOfDays)

        then:
        leaveCredits.remainingLeaveCredits == 25;
    }
}
