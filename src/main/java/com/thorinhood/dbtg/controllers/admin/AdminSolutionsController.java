package com.thorinhood.dbtg.controllers.admin;

import com.thorinhood.dbtg.models.Solution;
import com.thorinhood.dbtg.models.dto.SolutionDto;
import com.thorinhood.dbtg.repositories.SolutionsRepository;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/admin/solutions")
public class AdminSolutionsController {

    @Autowired
    private SolutionsRepository solutionsRepository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity addSolution(@RequestPart("json") SolutionDto solutionDto,
                                      @RequestPart("file") MultipartFile file) {
        try {
            solutionsRepository.save(Solution.fromDto(solutionDto, IOUtils.toByteArray(file.getInputStream())));
        } catch(IOException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/info", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<SolutionExtra>> getSolution(@RequestParam(value = "student", required = false) Long student,
                                                      @RequestParam(value = "task", required = false) Long task) {
        List<Solution> solutions = solutionsRepository.studentOrTask(student, task);
        return ResponseEntity.ok(solutions.stream().map(SolutionExtra::new).collect(Collectors.toList()));
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class SolutionExtra {
        private Long telegramId;
        private Integer taskId;
        private Date dateOfCompletion;

        public SolutionExtra(Solution solution) {
            this.telegramId = solution.getStudent();
            this.taskId = solution.getTask();
            this.dateOfCompletion = solution.getDateOfCompletion();
        }
    }

}
