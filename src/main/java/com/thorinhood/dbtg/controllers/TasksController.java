package com.thorinhood.dbtg.controllers;

import com.thorinhood.dbtg.models.Task;
import com.thorinhood.dbtg.models.filters.Filter;
import com.thorinhood.dbtg.repositories.TasksRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/tasks")
public class TasksController {

    @Autowired
    private TasksRepository tasksRepository;

    @GetMapping
    public String getInfoAboutAllTasks(@RequestParam(value = "title", required = false) String title,
                                       @RequestParam(value = "id", required = false) Long id,
                                       @RequestParam(value = "max_score_offline", required = false) Integer maxScoreOffline,
                                       @RequestParam(value = "max_score_online", required = false) Integer maxScoreOnline,
                                       @RequestParam(value = "deadline", required = false) String deadline,
                                       @RequestParam(value = "date_of_issue", required = false) String dateOfIssue,
                                       Model model) {
        Date deadlineDate = parseDate(deadline);
        Date dateOfIssueDate = parseDate(dateOfIssue);

        Filter<Task> filter = new Filter<Task>()
                .andIfNotNull("id", id, Filter::eq)
                .andIfNotNull("title", title, Filter::contains)
                .andIfNotNull("maxScoreOffline", maxScoreOffline, Filter::eq)
                .andIfNotNull("maxScoreOnline", maxScoreOnline, Filter::eq)
                .andIfNotNull("deadLine", deadlineDate, Filter::eq)
                .andIfNotNull("dateOfIssue", dateOfIssueDate, Filter::eq);

        List<Task> tasks = tasksRepository.findAll(filter.getFilter())
                .stream()
                .peek(x -> x.setTask(null))
                .collect(Collectors.toList());

        model.addAttribute("tasks", tasks);
        model.addAttribute("title", title);
        model.addAttribute("id", id);
        model.addAttribute("maxScoreOffline", maxScoreOffline);
        model.addAttribute("maxScoreOnline", maxScoreOnline);
        model.addAttribute("deadline", deadline);
        model.addAttribute("dateOfIssue", dateOfIssue);

        return "tasks";
    }

    @GetMapping(value = "/task", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getTask(@RequestParam Long id) {
        Optional<Task> practiceTask = tasksRepository.findById(id);
        if (practiceTask.isPresent()) {
            return ResponseEntity.ok().body(practiceTask.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/edit")
    public ResponseEntity editTask(@RequestParam("id") Long id,
                                   @RequestParam("title") String title,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("date_of_issue") Date dateOfIssue,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("deadLine") Date deadLine,
                                   @RequestParam("max_score_offline") Integer maxScoreOffline,
                                   @RequestParam("max_score_online") Integer maxScoreOnline,
                                   @RequestPart(value = "file", required = false) MultipartFile file) {
        Task task = Task.builder()
                .id(id)
                .title(title)
                .dateOfIssue(dateOfIssue)
                .deadLine(deadLine)
                .maxScoreOffline(maxScoreOffline)
                .maxScoreOnline(maxScoreOnline)
                .build();

        Optional<Task> toEdit = tasksRepository.findById(id);
        if (toEdit.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            if (file == null) {
                task.setTask(toEdit.get().getTask());
            } else {
                try {
                    task.setTask(IOUtils.toByteArray(file.getInputStream()));
                } catch (IOException e) {
                    return ResponseEntity.badRequest().build();
                }
            }
        }

        tasksRepository.save(task);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity addTask(@RequestParam("title") String title,
                                  @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("date_of_issue") Date dateOfIssue,
                                  @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("deadLine") Date deadLine,
                                  @RequestParam("max_score_offline") Integer maxScoreOffline,
                                  @RequestParam("max_score_online") Integer maxScoreOnline,
                                  @RequestPart("file") MultipartFile file) {
        Task task = Task.builder()
                .title(title)
                .dateOfIssue(dateOfIssue)
                .deadLine(deadLine)
                .maxScoreOffline(maxScoreOffline)
                .maxScoreOnline(maxScoreOnline)
                .build();
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

    private Date parseDate(String date) {
        if (date == null) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch(ParseException e) {
            return null;
        }
    }

}
