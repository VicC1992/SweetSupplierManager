package com.mybakery.sweet_suppliers.repository;

import com.mybakery.sweet_suppliers.Enums.RoleName;
import com.mybakery.sweet_suppliers.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
    boolean existsByName(RoleName name);
}
