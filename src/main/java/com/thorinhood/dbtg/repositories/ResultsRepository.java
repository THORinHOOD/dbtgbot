package com.thorinhood.dbtg.repositories;

import com.thorinhood.dbtg.models.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResultsRepository extends JpaRepository<Result, Object>, JpaSpecificationExecutor<Result> {

    @Query("SELECT r FROM com.thorinhood.dbtg.models.Result r " +
        "WHERE ((:student is null) or (:student = student)) and ((:task is null) or (:task = task))")
    List<Result> studentOrTask(@Param("student") String student, @Param("task") Integer task);

}
