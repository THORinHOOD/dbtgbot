package com.thorinhood.dbtg.models;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Embeddable
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

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "task")
    private byte[] task;

}
