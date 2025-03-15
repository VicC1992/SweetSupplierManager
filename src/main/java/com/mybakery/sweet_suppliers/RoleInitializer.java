package com.mybakery.sweet_suppliers;

import com.mybakery.sweet_suppliers.Enums.RoleName;
import com.mybakery.sweet_suppliers.entity.Role;
import com.mybakery.sweet_suppliers.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        addRoleIfNotExists(RoleName.ROLE_ADMIN);
        addRoleIfNotExists(RoleName.ROLE_PROCUREMENT_MANAGER);
        addRoleIfNotExists(RoleName.ROLE_WAREHOUSE_MANAGER);
    }

    private void addRoleIfNotExists(RoleName roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            System.out.println("Role added: " + roleName);
        }
    }
}
