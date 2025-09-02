package com.synacy.trainee.leavemanagementsystem.user;

import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCredits;
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;
    private final LeaveCreditsService leaveCreditsService;

    @Autowired
    public UserController(UserService userService, LeaveCreditsService leaveCreditsService) {
        this.userService = userService;
        this.leaveCreditsService = leaveCreditsService;
        userService.createInitialUsers();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/user")
    public UserResponseDTO addUser(@RequestBody UserRequestDTO userRequest){
        User user = userService.createUser(userRequest);
        LeaveCredits leaveCredits = leaveCreditsService.getLeaveCreditsOfUser(user).get();

        return new UserResponseDTO(user, leaveCredits);
    }

    @PutMapping("/api/v1/user/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userRequest){
        User user = userService.updateUser(id, userRequest);
        LeaveCredits leaveCredits = leaveCreditsService.getLeaveCreditsOfUser(user).get();

        return new UserResponseDTO(user, leaveCredits);
    }
}
