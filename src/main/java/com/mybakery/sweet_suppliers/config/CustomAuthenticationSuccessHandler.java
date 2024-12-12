package com.mybakery.sweet_suppliers.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isUser = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"));

        if (isAdmin) {
            response.sendRedirect("/admin/users");
        } else if (isUser) {
            response.sendRedirect("/users");
        } else {
            response.sendRedirect("/");
        }
    }
}
