package com.synacy.trainee.leavemanagementsystem.user;

import com.synacy.trainee.leavemanagementsystem.web.PageResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        userService.createInitialUsers();
    }

    @GetMapping("/api/v1/user/paginated")
    public PageResponse<UserResponseDTO> getUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        Page<User> userPage = userService.getUsers(page, size);

        List<UserResponseDTO> userDTOs = userPage.getContent().stream()
                .map(UserResponseDTO::new)
                .toList();

        int totalUsers = (int) userPage.getTotalElements();

        return new PageResponse<>(totalUsers, page, userDTOs);
    }

    @GetMapping("/api/v1/user")
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userService.getAllUsers();

        return users.stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    @GetMapping("/api/v1/user/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found!", id)));

        return new UserResponseDTO(user);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/user")
    public UserResponseDTO addUser(@RequestBody UserRequestDTO userRequest){
        User user = userService.createUser(userRequest);

        return new UserResponseDTO(user);
    }

    @PutMapping("/api/v1/user/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userRequest){
        User user = userService.updateUser(id, userRequest);

        return new UserResponseDTO(user);
    }

    @GetMapping("/api/v1/user/managers")
    public List<UserManagerResponseDTO> getAllManagers() {
        List<User> managers = userService.fetchAllManagers();

        return managers.stream()
                .map(UserManagerResponseDTO::new)
                .toList();
    }

}
