package com.thorinhood.dbtg.models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "tasks")
public class PracticeTask {

    @Id
    @Column(name = "nr")
    private Integer nr;

    @Column(name = "date_of_issue")
    private Date dateOfIssue;

    @Column(name = "deadline")
    private Date deadLine;

    @Column(name = "max_score_offline")
    private Integer maxScoreOffline;

    @Column(name = "max_score_online")
    private Integer maxScoreOnline;

}
