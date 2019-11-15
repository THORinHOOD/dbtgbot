package com.thorinhood.dbtg.repositories;

import com.thorinhood.dbtg.models.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SolutionsRepository extends JpaRepository<Solution, Object>, JpaSpecificationExecutor<Solution> {

    @Query("SELECT r FROM com.thorinhood.dbtg.models.Solution r " +
            "WHERE ((:student is null) or (:student = student)) and ((:task is null) or (:task = task))")
    List<Solution> studentOrTask(@Param("student") Long student, @Param("task") Long task);

}
