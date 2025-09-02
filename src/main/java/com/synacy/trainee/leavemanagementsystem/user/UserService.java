package com.synacy.trainee.leavemanagementsystem.user;

import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCredits;
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsRepository;
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final LeaveCreditsService leaveCreditsService;
    private final LeaveCreditsRepository leaveCreditsRepository; //for testing

    @Autowired
    public UserService(UserRepository userRepository, LeaveCreditsService leaveCreditsService,  LeaveCreditsRepository leaveCreditsRepository) {
        this.userRepository = userRepository;
        this.leaveCreditsService = leaveCreditsService;
        this.leaveCreditsRepository = leaveCreditsRepository;
    }

    public User createUser(UserRequestDTO userRequest) {
        User user = new User();

        User savedUser = saveUser(userRequest, user);

        if (savedUser.getRole() != UserRole.HR && userRequest.leaveCredits() != null) {
            leaveCreditsService.setLeaveCreditsForNewUsers(savedUser, userRequest); }

        return savedUser;
    }

    private User saveUser(UserRequestDTO userRequest, User user) {
        user.setName(userRequest.name());
        user.setRole(userRequest.role());

        if (userRequest.managerId() != null) {
            user.setManager(fetchManagerById(userRequest.managerId())); }

        return userRepository.save(user);
    }

    public User fetchManagerById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User updateUser(Long id, UserRequestDTO userRequest) {
        User user = getUserById(id).get();

        LeaveCredits leaveCredits = leaveCreditsService.getLeaveCreditsOfUser(user).get();

        leaveCredits.setTotalLeaveCredits(userRequest.leaveCredits());
        leaveCredits.setRemainingLeaveCredits(userRequest.leaveCredits());

        return saveUser(userRequest, user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    //for testing purposes
    public void createInitialUsers() {
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();

        LeaveCredits leaveCredits2 = new LeaveCredits();
        LeaveCredits leaveCredits3 = new LeaveCredits();

        user1.setName("HR");
        user1.setRole(UserRole.HR);

        user2.setName("MAN");
        user2.setRole(UserRole.MANAGER);
        leaveCredits2.setUser(user2);
        leaveCredits2.setTotalLeaveCredits(25);
        leaveCredits2.setRemainingLeaveCredits(25);

        user3.setName("EMP");
        user3.setRole(UserRole.EMPLOYEE);
        user3.setManager(user2);
        leaveCredits3.setUser(user3);
        leaveCredits3.setTotalLeaveCredits(25);
        leaveCredits3.setRemainingLeaveCredits(25);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        leaveCreditsRepository.save(leaveCredits2);
        leaveCreditsRepository.save(leaveCredits3);
    }
}
