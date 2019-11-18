package com.thorinhood.dbtg.repositories;

import com.thorinhood.dbtg.models.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SolutionsRepository extends JpaRepository<Solution, Object>, JpaSpecificationExecutor<Solution> {

    @Query("SELECT r FROM com.thorinhood.dbtg.models.Solution r " +
            "WHERE (:student = student) and (:task = task)")
    Optional<Solution> studentAndTask(@Param("student") long student, @Param("task") long task);

}
