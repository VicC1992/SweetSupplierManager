package com.mybakery.sweet_suppliers.config;

import com.mybakery.sweet_suppliers.service.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import java.io.IOException;
import java.util.Collection;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (userDetails.isMustChangePassword()) {
            response.sendRedirect("/change-password");
            return;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/home-page");
                return;
            } else if (role.equals("ROLE_PROCUREMENT_MANAGER")) {
                response.sendRedirect("/procurement-manager/home-page");
                return;
            } else if (role.equals("ROLE_WAREHOUSE_MANAGER")) {
                response.sendRedirect("/warehouse-manager/home-page");
                return;
            }
        }
        response.sendRedirect("/");
    }
}
