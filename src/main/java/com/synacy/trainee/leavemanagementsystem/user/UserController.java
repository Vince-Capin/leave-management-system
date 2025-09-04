package com.synacy.trainee.leavemanagementsystem.user;

import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCredits;
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsService;
import com.synacy.trainee.leavemanagementsystem.web.PageResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collector;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        userService.createInitialUsers();
    }

    @GetMapping("/api/v1/users")
    public PageResponse<UserResponseDTO> getUsers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        Page<User> userPage = userService.getUsers(page, size);

        List<UserResponseDTO> userDTOs = userPage.getContent().stream()
                .map(user -> {
                    LeaveCredits leaveCredits = leaveCreditsService.getLeaveCreditsOfUser(user).orElse(null);
                    return new UserResponseDTO(user, leaveCredits);})
                .toList();

        return new PageResponse<>((int) userPage.getTotalElements(), userPage.getNumber() + 1, userDTOs);
    }

    @GetMapping("/api/v1/users/dropdown")
    public List<UserDropdownDTO> getUsersForDropdown() {
        return userService.getAllUsers().stream()
                    .map(user -> new UserDropdownDTO(user.getId(), user.getName(), user.getRole()))
                    .toList();
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
