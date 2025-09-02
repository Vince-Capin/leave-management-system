package com.synacy.trainee.leavemanagementsystem.user;

import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCredits;
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;
    private final LeaveCreditsService leaveCreditsService;

    @Autowired
    public UserController(UserService userService, LeaveCreditsService leaveCreditsService) {
        this.userService = userService;
        this.leaveCreditsService = leaveCreditsService;
        userService.createInitialAdmin();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/user")
    public UserResponseDTO addUser(@RequestBody UserRequestDTO userRequest){
        User user = userService.createUser(userRequest);
        LeaveCredits leaveCredits = leaveCreditsService.getLeaveCreditsOfUsers(user).get();

        return new UserResponseDTO(user, leaveCredits);
    }
}
