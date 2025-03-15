package com.mybakery.sweet_suppliers.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/procurement-manager")
public class ProcurementManagerController {

    @GetMapping("/home-page")
    public String viewFirstPageProcurementManager() {
        return "procurement_manager_home_page";
    }
}
