package com.thorinhood.dbtg.repositories;

import com.thorinhood.dbtg.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TasksRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query(value = "select id from tasks", nativeQuery = true)
    List<Long> getAllIds();

}
