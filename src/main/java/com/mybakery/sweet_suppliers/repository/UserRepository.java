package com.mybakery.sweet_suppliers.repository;

import com.mybakery.sweet_suppliers.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    Optional<User> findByEmail(String email);
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.role.name = ?1")
    boolean existsByRoleName(String roleName);
    Optional<User> findById(Long id);
}
