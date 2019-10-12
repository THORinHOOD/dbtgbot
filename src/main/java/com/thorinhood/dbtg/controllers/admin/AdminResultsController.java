package com.thorinhood.dbtg.controllers.admin;

import com.thorinhood.dbtg.models.Result;
import com.thorinhood.dbtg.models.filters.Filter;
import com.thorinhood.dbtg.repositories.ResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/admin/results")
public class AdminResultsController {

    @Autowired
    private ResultsRepository resultsRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Result>> getResult(@RequestParam(required = false) Long student,
                                                  @RequestParam(required = false) Long task) {
        return ResponseEntity.ok(resultsRepository.studentOrTask(student, task));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity addResult(@RequestBody Result result) {
        try {
            resultsRepository.save(result);
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity deleteResult(@RequestParam String student,
                                       @RequestParam Integer task) {
        Filter<Result> filter = new Filter<Result>()
            .and("resultPK", new Result.ResultPK(student, task));
        List<Result> results = resultsRepository.findAll(filter.getFilter());
        if (results.size() != 1) {
            return ResponseEntity.badRequest().build();
        }
        resultsRepository.delete(results.get(0));
        return ResponseEntity.ok(results.get(0));
    }

}
