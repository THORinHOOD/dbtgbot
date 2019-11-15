package com.thorinhood.dbtg.controllers;

import com.thorinhood.dbtg.models.Solution;
import com.thorinhood.dbtg.models.Student;
import com.thorinhood.dbtg.models.Task;
import com.thorinhood.dbtg.models.dto.SolutionDto;
import com.thorinhood.dbtg.models.filters.Filter;
import com.thorinhood.dbtg.repositories.SolutionsRepository;
import com.thorinhood.dbtg.repositories.StudentsRepository;
import com.thorinhood.dbtg.repositories.TasksRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/solutions")
public class SolutionsController {

    @Autowired
    private SolutionsRepository solutionsRepository;

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private StudentsRepository studentsRepository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity addSolution(@RequestPart("json") SolutionDto solutionDto,
                                      @RequestPart("file") MultipartFile file) {
        /*try {
            solutionsRepository.save(Solution.fromDto(solutionDto, IOUtils.toByteArray(file.getInputStream())));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }*/
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<SolutionExtra>> getSolution(@RequestParam(value = "student", required = false) Long student,
                                                           @RequestParam(value = "task", required = false) Long task) {
        List<Solution> solutions = solutionsRepository.studentOrTask(student, task);
        return ResponseEntity.ok(solutions.stream().map(SolutionExtra::new).collect(Collectors.toList()));
    }

    @GetMapping(value = "/file", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getSolutionFile(@RequestParam("id") long id) {
        Optional<Solution> solution = solutionsRepository.findById(id);
        if (solution.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().body(solution.get().getSolution());
    }

    @GetMapping
    public String getSolutions(
            @RequestParam(value = "task_id") long taskId,
            @RequestParam(value = "student_id", required = false) Long studentId,
            @RequestParam(value = "student_lastname", required = false) String studentLastname,
            @RequestParam(value = "student_firstname", required = false) String studentFirstname,
            @RequestParam(value = "date_of_completion", required = false) String dateOfCompletionStr,
            @RequestParam(value = "student_group", required = false) String studentGroup,
            @RequestParam(value = "student_subgroup", required = false) Integer studentSubgroup,
            Map<String, Object> model) {

        Date dateOfCompletion = parseDate(dateOfCompletionStr);
        Optional<Task> taskOptional = tasksRepository.findById(taskId);

        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            List<Solution> solutions = task.getSolutions();
            List<SolutionInfo> solutionInfos = solutions.stream()
                    .map(solution -> {
                        if (!eq(getZeroTimeDate(solution.getDateOfCompletion()), getZeroTimeDate(dateOfCompletion))) {
                            return null;
                        }
                        Optional<Student> student = studentsRepository.findById(solution.getStudent());
                        if (student.isEmpty() ||
                                !contains(student.get().getLastName(), studentLastname) ||
                                !contains(student.get().getFirstName(), studentFirstname) ||
                                !eq(student.get().getTelegramId(), studentId) ||
                                !contains(student.get().getGroup(), studentGroup) ||
                                !eq(student.get().getSubGroup(), studentSubgroup)) {
                            return null;
                        }
                        return new SolutionInfo(solution, task, student.get());
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            model.put("solutions", solutionInfos);
            model.put("taskId", task.getId());
            model.put("taskTitle", task.getTitle());
        }

        model.put("studentLastname", studentLastname);
        model.put("studentFirstname", studentFirstname);
        model.put("studentId", studentId);
        model.put("dateOfCompletion", dateOfCompletionStr);
        model.put("studentGroup", studentGroup);
        model.put("studentSubgroup", studentSubgroup);

        return "solutions";
    }

    private boolean eq(Object origin, Object second) {
        if (origin == null) {
            return false;
        }
        if (second == null) {
            return true;
        }
        return origin.equals(second);
    }

    private boolean contains(String origin, String substr) {
        if (origin == null) {
            return false;
        }
        if (substr == null) {
            return true;
        }
        return origin.contains(substr);
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

    private static Date getZeroTimeDate(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();
        return date;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class SolutionInfo {
        private Long solutionId;
        private Long taskId;
        private String taskTitle;
        private Long studentId;
        private String studentGroup;
        private Integer studentSubgroup;
        private String studentFirstname;
        private String studentLastname;
        private Date dateOfSolution;

        public SolutionInfo(Solution solution, Task task, Student student) {
            solutionId = solution.getId();
            taskId = task.getId();
            studentGroup = student.getGroup();
            studentSubgroup = student.getSubGroup();
            taskTitle = task.getTitle();
            studentId = student.getTelegramId();
            studentFirstname = student.getFirstName();
            studentLastname = student.getLastName();
            dateOfSolution = solution.getDateOfCompletion();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class SolutionExtra {
        private Long telegramId;
        private Long taskId;
        private Date dateOfCompletion;

        public SolutionExtra(Solution solution) {
            this.telegramId = solution.getStudent();
            this.taskId = solution.getTask();
            this.dateOfCompletion = solution.getDateOfCompletion();
        }
    }

}