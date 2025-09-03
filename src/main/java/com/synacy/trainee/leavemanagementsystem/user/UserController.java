package com.synacy.trainee.leavemanagementsystem.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        userService.createInitialUsers();
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

}
