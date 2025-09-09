package com.synacy.trainee.leavemanagementsystem.user

import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsRepository
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsService
import spock.lang.Specification
import spock.util.mop.Use

class UserServiceSpec extends Specification{
    private UserRepository userRepository
    private LeaveCreditsService leaveCreditsService
    private LeaveCreditsRepository leaveCreditsRepository
    private UserService userService

    def setup() {
        userRepository = Mock(UserRepository)
        leaveCreditsService = Mock(LeaveCreditsService)
        leaveCreditsRepository = Mock(LeaveCreditsRepository)
        userService = new UserService(userRepository, leaveCreditsService, leaveCreditsRepository)
    }

    def "createUser() should create a new user and return the created user"(){
        given:
        UserRequestDTO userRequest = new UserRequestDTO(name: "New User", username: "newuser", role: UserRole.EMPLOYEE)
        User savedUser = new User(id: 1L, name: "New User", username: "newuser", role: UserRole.EMPLOYEE)

        userRepository.existsByUsername("newuser") >> false
        userRepository.save(_) >> savedUser

        when:
        User result = userService.createUser(userRequest)

        then:
        result.id == 1L
        result.name == "New User"
        result.username == "newuser"
        result.role == UserRole.EMPLOYEE
    }

    def "getAllUsers() return all users"() {
        given:
        User user1 = new User(id: 1L, name: "User One", role: UserRole.EMPLOYEE)
        User user2 = new User(id: 2L, name: "User Two", role: UserRole.MANAGER)
        List<User> users = [user1, user2]

        userRepository.findAll() >> users

        when:
        List<User> result = userService.getAllUsers()

        then:
        result[0].id == 1L
        result[0].name == "User One"
        result[0].role == UserRole.EMPLOYEE
        result[1].id == 2L
        result[1].name == "User Two"
        result[1].role == UserRole.MANAGER
    }

    def "createUser() should throw InvalidOperationException when username already exists"(){
//        given:
//        User existingUser = new User(id: 1L, name: "Existing User", username: "existinguser", role: UserRole.EMPLOYEE)
//        UserRequestDTO userRequest = new UserRequestDTO(name: "New User", role: UserRole.MANAGER)
    }

    def "fetchAllManagers() should all managers"(){
        given:
        User manager1 = new User(id: 1L, name: "Manager One", role: UserRole.MANAGER)
        User manager2 = new User(id: 2L, name: "Manager Two", role: UserRole.MANAGER)
        List<User> managers = [manager1, manager2]

        userRepository.findAllByRole(UserRole.MANAGER) >> managers

        when:
        List<User> result = userService.fetchAllManagers()

        then:
        result.size() == 2
        result[0].id == 1L
        result[0].name == "Manager One"
        result[0].role == UserRole.MANAGER
        result[1].id == 2L
        result[1].name == "Manager Two"
        result[1].role == UserRole.MANAGER
    }


}
