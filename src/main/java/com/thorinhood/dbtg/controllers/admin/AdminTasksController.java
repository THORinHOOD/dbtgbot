package com.thorinhood.dbtg.controllers.admin;

import com.thorinhood.dbtg.models.Task;
import com.thorinhood.dbtg.repositories.TasksRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/admin/tasks")
public class AdminTasksController {

    @Autowired
    private TasksRepository tasksRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getTask(@RequestParam Long id) {
        Optional<Task> practiceTask = tasksRepository.findById(id);
        if (practiceTask.isPresent()) {
            return ResponseEntity.ok().body(practiceTask.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity addTask(@RequestPart("json") Task task,
                                  @RequestPart("file") MultipartFile file) {
        try {
            task.setTask(IOUtils.toByteArray(file.getInputStream()));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
        tasksRepository.save(task);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/file", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getTaskFile(@RequestParam("id") long id) {
        Optional<Task> practiceTask = tasksRepository.findById(id);
        if (practiceTask.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(practiceTask.get().getTask());
    }

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getTask(@RequestParam("id") long id) {
        Optional<Task> practiceTask = tasksRepository.findById(id);
        if (practiceTask.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Task toReturn = practiceTask.get();
        toReturn.setTask(null);
        return ResponseEntity.ok().body(toReturn);
    }

    @DeleteMapping
    public ResponseEntity deleteTask(@RequestParam("id") long id) {
        Optional<Task> practiceTask = tasksRepository.findById(id);
        if (practiceTask.isEmpty()) {
            return ResponseEntity.badRequest().body("Not found.");
        }
        tasksRepository.delete(practiceTask.get());
        return ResponseEntity.ok().build();
    }

}
