package com.synacy.trainee.leavemanagementsystem.leaveCredits

import com.synacy.trainee.leavemanagementsystem.user.User
import com.synacy.trainee.leavemanagementsystem.user.UserRole
import spock.lang.Specification

class LeaveCreditsServiceSpec extends Specification {

    LeaveCreditsRepository leaveCreditsRepository
    LeaveCreditsService leaveCreditsService

    def setup () {
        leaveCreditsRepository = Mock(LeaveCreditsRepository)
        leaveCreditsService = new LeaveCreditsService(leaveCreditsRepository)
    }

    def "getLeaveCreditsOfUser() should return leave credits if found"() {
        given:
        def user = new User(id: 2L, name: "Bob", role: UserRole.EMPLOYEE)
        def leaveCredits = new LeaveCredits(user: user, totalLeaveCredits: 15, remainingLeaveCredits: 10)
        leaveCreditsRepository.findByUser(user) >> Optional.of(leaveCredits)

        when:
        def result = leaveCreditsService.getLeaveCreditsOfUser(user)

        then:
        result == leaveCredits
    }

    def "getLeaveCreditsOfUser() should throw exception if not found"() {
        given:
        def user = new User(id: 3L, name: "Charlie", role: UserRole.EMPLOYEE)
        leaveCreditsRepository.findByUser(user) >> Optional.empty()

        when:
        leaveCreditsService.getLeaveCreditsOfUser(user)

        then:
        def ex = thrown(LeaveCreditsNotFoundException)
        ex.message == "Leave credits for user 3 not found"
    }

//    def "setLeaveCreditsForNewUsers() should create and save leave credits with correct values"() {
//        given: "a user and a user request dto"
//        User user = new User()
//        def userRequest = Mock(UserRequestDTO) {
//            leaveCredits() >> 15
//        }
//
//        and: "expected leaveCredits object"
//        LeaveCredits savedLeaveCredits = new LeaveCredits(
//                user: user,
//                totalLeaveCredits: 15,
//                remainingLeaveCredits: 15
//        )
//
//        when: "the service is called"
//        leaveCreditsRepository.save(_ as LeaveCredits) >> { LeaveCredits lc -> lc } // return the same object passed
//        def result = leaveCreditsService.setLeaveCreditsForNewUsers(user, userRequest)
//
//        then: "repository save should be called once"
//        1 * leaveCreditsRepository.save(_ as LeaveCredits)
//
//        and: "the result should have correct values"
//        result.user == user
//        result.totalLeaveCredits == 15
//        result.remainingLeaveCredits == 15
//    }
//

}
