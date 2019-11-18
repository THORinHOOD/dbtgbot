package com.thorinhood.dbtg.models.plain;

import com.thorinhood.dbtg.models.Solution;
import com.thorinhood.dbtg.models.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MarkInfo {
    private Solution solution;
    private Task task;
}
