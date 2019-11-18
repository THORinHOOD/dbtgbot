package com.thorinhood.dbtg.services;

import com.thorinhood.dbtg.models.Solution;
import com.thorinhood.dbtg.models.filters.Filter;
import com.thorinhood.dbtg.models.plain.MarkInfo;
import com.thorinhood.dbtg.repositories.SolutionsRepository;
import com.thorinhood.dbtg.repositories.TasksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MarksService {

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private SolutionsRepository solutionsRepository;

    public List<MarkInfo> getStudentMarks(Long studentId) {
        Filter<Solution> solutionsFilter = new Filter<Solution>()
                .andEmbedded("solutionPK", "student", studentId, Filter::eqNested);
        List<Solution> solutions = solutionsRepository.findAll(solutionsFilter.getFilter());
        return solutions.stream()
                .filter(solution -> solution.getMark() != null)
                .map(solution -> tasksRepository.findById(solution.getSolutionPK().getTask()).map(task -> new MarkInfo(solution, task)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

}
