package com.synacy.trainee.leavemanagementsystem.user;

import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCredits;
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsRepository;
import com.synacy.trainee.leavemanagementsystem.leaveCredits.LeaveCreditsService;
import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveApplication;
import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveApplicationService;
import com.synacy.trainee.leavemanagementsystem.leaveapplication.LeaveStatus;
import com.synacy.trainee.leavemanagementsystem.web.apierror.InvalidOperationException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final LeaveCreditsService leaveCreditsService;
    private final LeaveApplicationService leaveApplicationService;
    private final LeaveCreditsRepository leaveCreditsRepository; //for testing

    @Autowired
    public UserService(UserRepository userRepository,
                       LeaveCreditsService leaveCreditsService,
                       LeaveCreditsRepository leaveCreditsRepository,
                       LeaveApplicationService leaveApplicationService) {
        this.userRepository = userRepository;
        this.leaveCreditsService = leaveCreditsService;
        this.leaveApplicationService = leaveApplicationService;
        this.leaveCreditsRepository = leaveCreditsRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Transactional
    public User createUser(UserRequestDTO userRequest) {
        if (checkForSameUserName(userRequest.name(), null)) {
            throw new InvalidOperationException(
                    "SAME_USER_NAME", "Same user name already exists"
            );
        }

        User user = new User();
        User savedUser = saveUser(userRequest, user);

        if (savedUser.getRole() != UserRole.HR && userRequest.leaveCredits() != null) {
            LeaveCredits leaveCredits = leaveCreditsService.setLeaveCreditsForNewUsers(savedUser, userRequest);
            savedUser.setLeaveCredits(leaveCredits);
        }

        return savedUser;
    }

    @Transactional
    public User saveUser(UserRequestDTO userRequest, User user) {
        user.setName(userRequest.name());
        user.setRole(userRequest.role());

        if (userRequest.managerId() != null) {
            User manager = fetchManagerById(userRequest.managerId())
                    .orElseThrow(() -> new UserNotFoundException("Manager not found"));
            user.setManager(manager);
        }

        return userRepository.save(user);
    }

    public Page<User> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size).withSort(Sort.by(Sort.Direction.ASC, "id"));
        return userRepository.findAll(pageable);
    }
  
    public Optional<User> fetchManagerById(Long id) {
        return userRepository.findById(id);

    }

    @Transactional
    public User updateUser(Long id, UserRequestDTO userRequest) {
        if (checkForSameUserName(userRequest.name(), id)) {
            throw new InvalidOperationException(
                    "SAME_USER_NAME", "Same user name already exists"
            );
        }

        User user = getUserById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id %d not found!", id)));

        if (user.getRole() != UserRole.HR && userRequest.leaveCredits() != null) {
            LeaveCredits leaveCredits = leaveCreditsService.getLeaveCreditsOfUser(user);
            leaveCredits.setTotalLeaveCredits(userRequest.leaveCredits());
            leaveCredits.setRemainingLeaveCredits(userRequest.leaveCredits());
            user.setLeaveCredits(leaveCredits);
        }

        if(user.getRole() == UserRole.MANAGER && (userRequest.role() ==  UserRole.EMPLOYEE || userRequest.role() ==  UserRole.HR)){
            List<User> users = fetchAllEmployeesUnderManager(user.getId());
            users.forEach(User -> User.setManager(null));
            setManagerOfEmployeesToNUll(users);

            List<LeaveApplication> leaveApplications = this.leaveApplicationService.getActiveLeaveApplicationsByManagerId(user.getId(), LeaveStatus.PENDING);
            leaveApplications.forEach(leaveApplication -> leaveApplication.setManager(null));
            this.leaveApplicationService.setManagerToNull(leaveApplications);
        }

        return saveUser(userRequest, user);
    }

    public Optional<User> getUserById(Long id) { return userRepository.findById(id); }

    public boolean checkForSameUserName (String name, Long id) {
        if (id == null) {
            return userRepository.existsByName(name);
        }

        return userRepository.existsByNameIgnoreCaseAndIdNot(name, id);
    }

    public List<User> fetchAllManagers() {
        return userRepository.findAllByRole(UserRole.MANAGER);
    }

    public List<User> fetchAllEmployeesUnderManager(Long managerId) {
        return userRepository.findALlByManager_Id(managerId);
    }

    public void setManagerOfEmployeesToNUll(List<User> users) {
        userRepository.saveAll(users);
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
