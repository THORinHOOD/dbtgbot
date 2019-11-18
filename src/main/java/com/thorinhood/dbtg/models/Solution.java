package com.thorinhood.dbtg.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Embeddable
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "solutions")
public class Solution {

    @EmbeddedId
    private SolutionPK solutionPK;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "solution")
    private byte[] solution;

    @Column(name = "date_of_completion")
    private Date dateOfCompletion;

    @Column(name = "mark")
    private Integer mark;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SolutionPK implements Serializable {
        private Long student;
        private Long task;
    }

}
