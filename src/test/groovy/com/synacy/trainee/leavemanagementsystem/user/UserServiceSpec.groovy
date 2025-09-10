package com.synacy.trainee.leavemanagementsystem.user

import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCredits
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsRepository
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsService
import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveApplication
import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveApplicationService
import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveStatus
import com.synacy.trainee.leavemanagementsystem.web.apierror.InvalidOperationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

class UserServiceSpec extends Specification{
    UserRepository userRepository
    LeaveCreditsService leaveCreditsService
    LeaveCreditsRepository leaveCreditsRepository
    UserService userService
    LeaveApplicationService leaveApplicationService

    def setup() {
        userRepository = Mock(UserRepository)
        leaveCreditsService = Mock(LeaveCreditsService)
        leaveCreditsRepository = Mock(LeaveCreditsRepository)
        leaveApplicationService = Mock(LeaveApplicationService)
        userService = new UserService(userRepository, leaveCreditsService, leaveCreditsRepository, leaveApplicationService)
    }

    def "getAllUsers() should return all users sorted by id in ascending order"() {
        given:
        User user1 = new User(id: 1L, name: "User One", role: UserRole.EMPLOYEE)
        User user2 = new User(id: 2L, name: "User Two", role: UserRole.MANAGER)
        List<User> users = [user1, user2]

        userRepository.findAll(Sort.by(Sort.Direction.ASC, "id")) >> users

        when:
        List<User> result = userService.getAllUsers()

        then:
        result.size() == 2
        result[0].id == user1.id
        result[0].name == user1.name
        result[0].role == user1.role
        result[1].id == user2.id
        result[1].name == user2.name
        result[1].role == user2.role
    }

    def "getAllUsers() should return an empty list when no users exist"() {
        given:
        userRepository.findAll(Sort.by(Sort.Direction.ASC, "id")) >> []

        when:
        List<User> result = userService.getAllUsers()

        then:
        result.isEmpty()
    }

    def "createUser() should create a user with leave credits when role is not HR and leave credits are provided"() {
        given:
        Long userId = 1L
        UserRequestDTO userRequest = new UserRequestDTO("UpdatedUser", UserRole.EMPLOYEE, 20, null)
        User user = new User(id: userId, name: "OldUser", role: UserRole.EMPLOYEE)
        LeaveCredits leaveCredits = new LeaveCredits(totalLeaveCredits: 10, remainingLeaveCredits: 5)

        userRepository.findById(userId) >> Optional.of(user)
        leaveCreditsService.getLeaveCreditsOfUser(user) >> leaveCredits
        userRepository.save(_) >> user

        when:
        User result = userService.updateUser(userId, userRequest)

        then:
        result.name == "UpdatedUser"
        result.role == UserRole.EMPLOYEE
        result.leaveCredits.totalLeaveCredits == 20
        result.leaveCredits.remainingLeaveCredits == 20
    }

    def "createUser() should throw InvalidOperationException when username already exists"() {
        given:
        UserRequestDTO userRequest = new UserRequestDTO("DuplicateUser", UserRole.EMPLOYEE, 10, null)

        userRepository.existsByName("DuplicateUser") >> true

        when:
        userService.createUser(userRequest)

        then:
        thrown (InvalidOperationException)
    }

    def "createUser() should create a user without leave credits when role is HR"() {
        given:
        UserRequestDTO userRequest = new UserRequestDTO("HRUser", UserRole.HR, null, null)
        User user = new User(name: "HRUser", role: UserRole.HR)

        userRepository.existsByName("HRUser") >> false
        userRepository.save(_) >> user

        when:
        User result = userService.createUser(userRequest)

        then:
        result.name == "HRUser"
        result.role == UserRole.HR
        result.leaveCredits == null
    }

    def "getUsers() should return pageable list of all users"() {
        given:
        User user1 = new User(id: 1L, name: "User One", role: UserRole.EMPLOYEE)
        User user2 = new User(id: 2L, name: "User Two", role: UserRole.MANAGER)
        List<User> users = [user1, user2]
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "id"))
        PageImpl<User> userPage = new PageImpl<>(users, pageRequest, users.size())

        userRepository.findAll(pageRequest) >> userPage

        when:
        Page<User> result = userService.getUsers(1, 2)

        then:
        result.content.size() == 2
        result.content[0].id == user1.id
        result.content[0].name == user1.name
        result.content[0].role == user1.role
        result.content[1].id == user2.id
        result.content[1].name == user2.name
        result.content[1].role == user2.role

    }

    def "getUsers() should return an empty page when no users exist"() {
        given:
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "id"))
        PageImpl<User> emptyPage = new PageImpl<>([], pageRequest, 0)

        userRepository.findAll(pageRequest) >> emptyPage

        when:
        Page<User> result = userService.getUsers(1, 2)

        then:
        result.content.isEmpty()
        result.totalElements == 0
    }

    def "fetchManagerById() should return optional manager when exists"(){
        given:
        Long managerId = 1L
        User manager = new User(id: managerId, name: "Manager One", role: UserRole.MANAGER)

        userRepository.findById(managerId) >> Optional.of(manager)

        when:
        Optional<User> result = userService.fetchManagerById(managerId)

        then:
        result.isPresent()
        result.get().id == managerId
        result.get().name == manager.name
        result.get().role == manager.role
    }

    def "updateUser() should update user details and leave credits when role is not HR"() {
        given:
        Long userId = 1L
        UserRequestDTO userRequest = new UserRequestDTO("UpdatedUser", UserRole.EMPLOYEE, 15, null)
        User user = new User(id: userId, name: "OldUser", role: UserRole.EMPLOYEE)
        LeaveCredits leaveCredits = new LeaveCredits(totalLeaveCredits: 10, remainingLeaveCredits: 5)

        userRepository.findById(userId) >> Optional.of(user)
        leaveCreditsService.getLeaveCreditsOfUser(user) >> leaveCredits
        userRepository.save(_) >> user

        when:
        User result = userService.updateUser(userId, userRequest)

        then:
        result.name == "UpdatedUser"
        result.role == UserRole.EMPLOYEE
        result.leaveCredits.totalLeaveCredits == 15
        result.leaveCredits.remainingLeaveCredits == 15
    }

    def "updateUser() should throw UserNotFoundException when user does not exist"() {
        given:
        Long userId = 1L
        UserRequestDTO userRequest = new UserRequestDTO("NonExistentUser", UserRole.EMPLOYEE, 10, null)

        userRepository.findById(userId) >> Optional.empty()

        when:
        userService.updateUser(userId, userRequest)

        then:
        thrown(UserNotFoundException)
    }

    def "updateUser() should throw InvalidOperationException when username already exists"() {
        given:
        Long userId = 1L
        UserRequestDTO userRequest = new UserRequestDTO("DuplicateUser", UserRole.EMPLOYEE, 10, null)
        User user = new User(id: userId, name: "DuplicateUser", role: UserRole.EMPLOYEE)

        userRepository.findById(userId) >> Optional.of(user)
        userRepository.existsByNameIgnoreCaseAndIdNot("DuplicateUser", userId) >> true

        when:
        userService.updateUser(userId, userRequest)

        then:
        thrown(InvalidOperationException)
    }

    def "updateUser() should remove manager and active leave applications when role changes from manager"() {
        given:
        Long userId = 1L
        UserRequestDTO userRequest = new UserRequestDTO("ManagerToEmployee", UserRole.EMPLOYEE, null, null)
        User manager = new User(id: userId, name: "Manager", role: UserRole.MANAGER)
        User employee1 = new User(id: 2L, name: "Employee1", role: UserRole.EMPLOYEE, manager: manager)
        User employee2 = new User(id: 3L, name: "Employee2", role: UserRole.EMPLOYEE, manager: manager)
        LeaveApplication leaveApplication = new LeaveApplication(manager: manager, status: LeaveStatus.PENDING)

        userRepository.findById(userId) >> Optional.of(manager)
        userRepository.findALlByManager_Id(userId) >> [employee1, employee2]
        leaveApplicationService.getActiveLeaveApplicationsByManagerId(userId, LeaveStatus.PENDING) >> [leaveApplication]
        userRepository.save(_) >> manager

        when:
        User result = userService.updateUser(userId, userRequest)

        then:
        result.name == "ManagerToEmployee"
        result.role == UserRole.EMPLOYEE
        employee1.manager == null
        employee2.manager == null
        leaveApplication.manager == null
    }

    def  "getUserById() should return optional user by Id"(){
        given:
        Long userId = 1L
        User user = new User(id: 1L, name: "User One", role: UserRole.EMPLOYEE)

        userRepository.findById(userId) >> Optional.of(user)

        when:
        Optional<User> result = userService.getUserById(userId)
        then:
        result.isPresent()
        result.get().id == user.id
        result.get().name == user.name
        result.get().role == user.role

    }

    def "getUserById() should return empty optional when user does not exist"(){
        given:
        Long userId = 1L

        userRepository.findById(userId) >> Optional.empty()

        when:
        Optional<User> result = userService.getUserById(userId)

        then:
        !result.isPresent()
    }

    def "checkForSameUserName() should return true if id is null and when the user exist"(){
        given:
        String name = "Troy"

        1 * userRepository.existsByName(name) >> true

        when:
        boolean result = userService.checkForSameUserName(name, null)

        then:
        result
    }

    def "checkForSameUserName() should return false if id is null and when the user does not exist"(){
        given:
        String name = "Troy"

        1 * userRepository.existsByName(name) >> false

        when:
        boolean result = userService.checkForSameUserName(name, null)

        then:
        !result
    }

    def "checkForSameUserName() should return true if username already exist with different id"(){
        given:
        String name = "Troy"
        Long id = 1L

        1 * userRepository.existsByNameIgnoreCaseAndIdNot(name, id) >> true

        when:
        boolean result = userService.checkForSameUserName(name, id)

        then:
        result
    }

    def "checkForSameUserName() should return false if username does not exist with different id"(){
        given:
        String name = "Troy"
        Long id = 1L

        1 * userRepository.existsByNameIgnoreCaseAndIdNot(name, id) >> false

        when:
        boolean result = userService.checkForSameUserName(name, id)

        then:
        !result
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
        result[0].id == manager1.id
        result[0].name == manager1.name
        result[0].role == manager1.role
        result[1].id == manager2.id
        result[1].name == manager2.name
        result[1].role == manager2.role
    }

    def "fetchAllManagers() should return empty list when no manager exist"(){
        given:
        userRepository.findAllByRole(UserRole.MANAGER) >> []

        when:
        List<User> result = userService.fetchAllManagers()

        then:
        result.isEmpty()
    }

    def "fetchAllEmployeesUnderManager() should return employees manage by manager"(){
        given:
        Long managerId = 1L
        User manager = new User(id: managerId, name: "Manager One", role: UserRole.MANAGER)
        User employee1 = new User(id: 2L, name: "Employee One", role: UserRole.EMPLOYEE, manager: manager)
        User employee2 = new User(id: 3L, name: "Employee Two", role: UserRole.EMPLOYEE, manager: manager)
        List<User> employees = [employee1, employee2]

        1 * userRepository.findALlByManager_Id(managerId) >> employees

        when:
        List<User> result = userService.fetchAllEmployeesUnderManager(managerId)

        then:
        result[0].id == employee1.id
        result[0].name == employee1.name
        result[0].role == employee1.role
        result[0].manager.id == managerId
        result[1].id == employee2.id
        result[1].name == employee2.name
        result[1].role == employee2.role
        result[1].manager.id == managerId
    }

    def "fetchAllEmployeesUnderManager() should return empty list when no employee exist under manager"(){
        given:
        Long managerId = 1L

        1 * userRepository.findALlByManager_Id(managerId) >> []

        when:
        List<User> result = userService.fetchAllEmployeesUnderManager(managerId)

        then:
        result.isEmpty()
    }

    def "setManagerOfEmployeesToNull should save all users with null manager"() {
        given:
        User employee1 = new User(id: 1L, name: "Employee One", role: UserRole.EMPLOYEE, manager: null)
        User employee2 = new User(id: 2L, name: "Employee Two", role: UserRole.EMPLOYEE, manager: null)
        List<User> employees = [employee1, employee2]

        1 * userRepository.saveAll(employees) >> employees

        when:
        userService.setManagerOfEmployeesToNUll(employees)

        then:
        noExceptionThrown()
    }

    def "setManagerOfEmployeesToNull should handle empty list of users"() {
        given:
        List<User> employees = []

        1 * userRepository.saveAll(employees) >> employees

        when:
        userService.setManagerOfEmployeesToNUll(employees)

        then:
        noExceptionThrown()
    }

}
