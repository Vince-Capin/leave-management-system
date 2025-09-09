package com.synacy.trainee.leavemanagementsystem.user

import com.synacy.trainee.leavemanagementsystem.web.PageResponse
import org.springframework.data.domain.Page
import spock.lang.Specification
import spock.util.mop.Use

class UserControllerSpec extends Specification {

    UserController userController;
    UserService userService;

    def setup() {
        userService = Mock(UserService)
        userController = new UserController(userService)
    }

    def "getUsers should return a paginated list of users given the page and size parameters"(){
        given:
        int page = 1
        int size = 5
        int totalCount = 2
        User user1 = new User(id: 1L, name: "User One", role: UserRole.EMPLOYEE)
        User user2 = new User(id: 2L, name: "User Two", role: UserRole.MANAGER)

        Page<User> users = Mock()
        users.getContent() >> [user1, user2]
        users.getTotalElements() >> totalCount
        userService.getUsers(page, size) >> users

        when:
        PageResponse<UserResponseDTO> response = userController.getUsers(page, size)

        then:
        page == response.pageNumber()
        totalCount == response.totalCount()
        totalCount == response.content().size()
        user1.id == response.content()[0].id
        user1.name == response.content()[0].name
        user1.role == response.content()[0].role
        user2.id == response.content()[1].id
        user2.name == response.content()[1].name
        user2.role == response.content()[1].role

    }

    def "getAllUsers should return list of users" (){
        given:
        User user1 = new User(id: 1L, name: "User One", role: UserRole.EMPLOYEE)
        User user2 = new User(id: 2L, name: "User Two", role: UserRole.MANAGER)
        List<User> users = [user1, user2]

        userService.getAllUsers() >> users

        when:
        List<UserResponseDTO> response = userController.getAllUsers()

        then:
        user1.id == response[0].id
        user1.name == response[0].name
        user1.role == response[0].role
        user2.id == response[1].id
        user2.name == response[1].name
        user2.role == response[1].role

    }

    def "getUserById should throw UserNotFoundException when user does not exist"(){
        given:
        Long userId = 2L
        userService.getUserById(userId) >> Optional.empty()

        when:
        userController.getUserById(userId)

        then:
        thrown (UserNotFoundException)
    }

    def "getUserById should return user when exists"(){
        given:
        Long userId = 1L
        User user = new User(id: userId, name: "User One", role: UserRole.EMPLOYEE)

        userService.getUserById(userId) >> Optional.of(user)

        when:
        UserResponseDTO result = userController.getUserById(userId)

        then:
        user.id == result.id
        user.name == result.name
        user.role == result.role
    }

    def "addUser should create a new user and return the user"(){
        given:
        UserRequestDTO request = new UserRequestDTO(name: "New User", role: UserRole.EMPLOYEE)
        User createdUser = new User(id: 1L, name: request.name, role: request.role)

        1 * userService.addUser(request) >> createdUser

        when:
        UserResponseDTO result = userController.addUser(request)

        then:
        result.id == createdUser.id
        result.name == createdUser.name
        result.role == createdUser.role
    }

    def "updateUser should update the details of an existing user and return the updated value" () {
        given:
        Long userId = 1L
        UserRequestDTO request = new UserRequestDTO("Updated User", UserRole.MANAGER, null, null)
        User updatedUser = new User(id: userId, name: request.name(), role: request.role())

        1 * userService.updateUser(userId, request) >> Optional.of(updatedUser)

        when:
        UserResponseDTO result = userController.updateUser(userId, request)

        then:
        result.id == updatedUser.id
        result.name == updatedUser.name
        result.role == updatedUser.role
    }


    def "getAllManagers should list of managers"() {
        given:
        User manager1 = Mock()
        User manager2 = Mock()
        manager1.id >> 1L
        manager1.name >> "Manager one"
        manager2.id >> 2L
        manager2.name >> "Manager Two"

        List<User> managers = [manager1, manager2]

        1 * userService.fetchAllManagers() >> managers

        when:
        List<UserManagerResponseDTO> response = userController.getAllManagers()

        then:
        manager1.id == response[0].id
        manager1.name == response[0].name
        manager2.id == response[1].id
        manager2.name == response[1].name
    }
}







