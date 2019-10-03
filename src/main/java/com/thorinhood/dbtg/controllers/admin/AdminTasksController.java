package com.thorinhood.dbtg.controllers.admin;

import com.google.inject.internal.asm.$TypePath;
import com.thorinhood.dbtg.models.PracticeTask;
import com.thorinhood.dbtg.repositories.PracticeTasksRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.io.IOException;
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

    @PostMapping
    public ResponseEntity addTask(@RequestPart("json") PracticeTask task,
                                  @RequestPart("file") MultipartFile file) {
        try {
            task.setTask(IOUtils.toByteArray(file.getInputStream()));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
        practiceTasksRepository.save(task);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/file", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getTaskFile(@RequestParam("nr") int number) {
        Optional<PracticeTask> practiceTask = practiceTasksRepository.findById(number);
        if (practiceTask.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(practiceTask.get().getTask());
    }

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getTask(@RequestParam("nr") int number) {
        Optional<PracticeTask> practiceTask = practiceTasksRepository.findById(number);
        if (practiceTask.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        PracticeTask toReturn = practiceTask.get();
        toReturn.setTask(null);
        return ResponseEntity.ok().body(toReturn);
    }

}
