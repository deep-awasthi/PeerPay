package com.peerpay.user.repository;

import com.peerpay.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUpiId(String upiId);
    Optional<User> findByPhone(String phone);
}
