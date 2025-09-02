package com.synacy.trainee.leavemanagementsystem.user;

import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LeaveCreditsService leaveCreditsService;

    @Autowired
    public UserService(UserRepository userRepository, LeaveCreditsService leaveCreditsService) {
        this.userRepository = userRepository;
        this.leaveCreditsService = leaveCreditsService;
    }

    public User createUser(UserRequestDTO userRequest) {
        User user = new User();

        user.setName(userRequest.name());
        user.setRole(userRequest.role());

        if (userRequest.managerId() != null) {
            user.setManager(fetchManagerById(userRequest.managerId())); }

        User savedUser = userRepository.save(user);

        if (userRequest.role() != UserRole.HR) {
            leaveCreditsService.setLeaveCreditsForNewUsers(user, userRequest.leaveCredits()); }

        return savedUser;
    }

    public User fetchManagerById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
