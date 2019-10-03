package com.thorinhood.dbtg.repositories;

import com.thorinhood.dbtg.models.PracticeTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PracticeTasksRepository extends JpaRepository<PracticeTask, Integer>, JpaSpecificationExecutor<PracticeTask> {

    @Query(value = "select nr from tasks", nativeQuery = true)
    List<Integer> getAllIds();

}
