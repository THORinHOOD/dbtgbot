package com.thorinhood.dbtg.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SolutionDto {
    private Long telegramId;
    private Long taskId;
}
