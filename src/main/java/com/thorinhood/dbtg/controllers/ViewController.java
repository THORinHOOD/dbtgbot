package com.thorinhood.dbtg.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
public class ViewController {

    @GetMapping("/index")
    public String getIndex() {
        return "index";
    }

}
