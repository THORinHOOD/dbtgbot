package com.thorinhood.dbtg.repositories;

import com.thorinhood.dbtg.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StudentsRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
}
