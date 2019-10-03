package com.thorinhood.dbtg.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "results")
public class Result {

    @EmbeddedId
    private ResultPK resultPK;

    @Column(name = "when_did")
    private Date whenDid;

    @Column(name = "mark")
    private Integer mark;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResultPK implements Serializable {
        private String student;
        private Integer task;
    }

}


