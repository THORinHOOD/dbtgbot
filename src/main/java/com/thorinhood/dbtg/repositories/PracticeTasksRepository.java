package com.thorinhood.dbtg.repositories;

import com.thorinhood.dbtg.models.PracticeTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PracticeTasksRepository extends JpaRepository<PracticeTask, Integer>, JpaSpecificationExecutor<PracticeTask> {
}
