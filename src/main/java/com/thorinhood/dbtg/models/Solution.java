package com.thorinhood.dbtg.models;

import com.thorinhood.dbtg.models.dto.SolutionDto;
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

    public static Solution fromDto(SolutionDto solutionDto, byte[] solution) {
        return new Solution(solutionDto.getTelegramId(), solutionDto.getTaskId(), solution, new Date());
    }

    @Id
    @Column(name = "student")
    private Long student;

    @Column(name = "task")
    private Integer task;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "solution")
    private byte[] solution;

    @Column(name = "date_of_completion")
    private Date dateOfCompletion;


}
