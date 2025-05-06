package com.mybakery.sweet_suppliers.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    @GetMapping("")
    public String viewHomePage() {
        return "main_page";
    }
}
