package com.mybakery.sweet_suppliers.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.mybakery.sweet_suppliers.repository.UserRepository;

@Controller
public class AppController {

    @GetMapping("")
    public String viewHomePage() {
        return "main_page";
    }
}
