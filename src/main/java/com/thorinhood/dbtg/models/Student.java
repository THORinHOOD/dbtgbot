package com.thorinhood.dbtg.models;

import com.thorinhood.dbtg.models.dto.SolutionDto;
import lombok.Data;
import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

import javax.persistence.*;
import java.util.List;

@CsvDataType
@Data
@Entity
@Table(name = "students")
public class Student {

    @Id
    @Column(name = "telegram_id")
    @CsvField(pos = 1)
    private Long telegramId;

    @CsvField(pos = 2)
    @Column(name = "email")
    private String email;

    @CsvField(pos = 3)
    @Column(name = "first_name")
    private String firstName;

    @CsvField(pos = 4)
    @Column(name = "last_name")
    private String lastName;

    @CsvField(pos = 5)
    @Column(name = "group_nr")
    private String group;

    @CsvField(pos = 6)
    @Column(name = "sub_group_nr")
    private int subGroup;



}
