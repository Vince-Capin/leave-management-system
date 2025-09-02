package com.synacy.trainee.leavemanagementsystem.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/v1/users")
    public UserResponseDTO addUser(@RequestBody UserRequestDTO userRequest){
        System.out.println(userRequest);
        User user = userService.createUser(userRequest);

        return new UserResponseDTO(user, userRequest.leaveCredits());
    }
}
