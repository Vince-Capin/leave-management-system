package com.synacy.trainee.leavemanagementsystem.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByName(String name);
    List<User> findAllByRole(UserRole role);
}

