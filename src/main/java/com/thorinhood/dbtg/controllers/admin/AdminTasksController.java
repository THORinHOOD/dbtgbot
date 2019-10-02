package com.thorinhood.dbtg.controllers.admin;

import com.thorinhood.dbtg.models.PracticeTask;
import com.thorinhood.dbtg.repositories.PracticeTasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/api/v1/admin/tasks")
public class AdminTasksController {

    @Autowired
    private PracticeTasksRepository practiceTasksRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getTask(@RequestParam Integer nr) {
        Optional<PracticeTask> practiceTask = practiceTasksRepository.findById(nr);
        if (practiceTask.isPresent()) {
            return ResponseEntity.ok().body(practiceTask.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
