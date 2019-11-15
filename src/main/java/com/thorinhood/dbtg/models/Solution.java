package com.thorinhood.dbtg.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Embeddable
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "solutions")
public class Solution {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "student")
    private Long student;

    @Column(name = "task")
    private Long task;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "solution")
    private byte[] solution;

    @Column(name = "date_of_completion")
    private Date dateOfCompletion;

}
