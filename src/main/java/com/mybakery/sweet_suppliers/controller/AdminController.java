package com.mybakery.sweet_suppliers.controller;
import com.mybakery.sweet_suppliers.Enums.RoleName;
import com.mybakery.sweet_suppliers.dto.UserDto;
import com.mybakery.sweet_suppliers.entity.Role;
import com.mybakery.sweet_suppliers.entity.User;
import com.mybakery.sweet_suppliers.repository.RoleRepository;
import com.mybakery.sweet_suppliers.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")

public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/home-page")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin_home_page";
    }

    @GetMapping("/create-edit-user")
    public String showCreateOrEditUserForm(@RequestParam(required = false) Long id, Model model) {
        UserDto userDto;
        if (id != null) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setEmail(user.getEmail());
            userDto.setFirstName(user.getFirstName());
            userDto.setLastName(user.getLastName());
            userDto.setRole(user.getRole().getName().name());
        } else {
            userDto = new UserDto();
        }
        model.addAttribute("userDto", userDto);
        return "create_edit_user";
    }

    @PostMapping("/save-user")
    public String saveOrUpdateUser(@ModelAttribute UserDto userDto) {
        User user;

        if (userDto.getId() != null) {
            user = userRepository.findById(userDto.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setEmail(userDto.getEmail());

            Role role = roleRepository.findByName(RoleName.valueOf(userDto.getRole()))
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
        } else {
            user = new User();
            user.setEmail(userDto.getEmail());
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());

            Role role = roleRepository.findByName(RoleName.valueOf(userDto.getRole()))
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
            user.setMustChangePassword(true);
        }
        userRepository.save(user);
        return "redirect:/admin/home-page";
    }

    @GetMapping("/delete-user")
    public String deleteUser(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getRole().getName() == RoleName.ROLE_ADMIN) {
                redirectAttributes.addFlashAttribute("errorMessage", "Admin users cannot be deleted.");
                return "redirect:/admin/home-page";
            }
            userRepository.deleteById(id);
        }
        return "redirect:/admin/home-page";
    }

    @GetMapping("/reset-password")
    public String resetUserPassword(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getRole().getName() == RoleName.ROLE_ADMIN) {
                redirectAttributes.addFlashAttribute("errorMessage", "You cannot reset the password of an Admin.");
                return "redirect:/admin/home-page";
            }

            user.setPassword(passwordEncoder.encode("00000"));
            user.setMustChangePassword(true);
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("successMessage", "Password was reset to '00000'.");
        }

        return "redirect:/admin/home-page";
    }

    @GetMapping("/change-own-password")
    public String showChangeOwnPasswordForm() {
        return "change_own_password";
    }

    @PostMapping("/change-own-password")
    public String changeOwnPassword(@RequestParam String currentPassword,
                                    @RequestParam String newPassword,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Current password is incorrect.");
            return "redirect:/admin/change-own-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
        return "redirect:/admin/home-page";
    }

}
