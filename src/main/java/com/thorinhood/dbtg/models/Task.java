package com.thorinhood.dbtg.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Embeddable
@Data
@Entity
@Table(name = "tasks")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

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

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<Solution> solutions;

}
