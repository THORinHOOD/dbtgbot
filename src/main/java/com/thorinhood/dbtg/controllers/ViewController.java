package com.thorinhood.dbtg.controllers;

import com.thorinhood.dbtg.models.Student;
import com.thorinhood.dbtg.repositories.StudentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/v1")
public class ViewController {

    @GetMapping("/index")
    public String getIndex() {
        return "index";
    }

}
