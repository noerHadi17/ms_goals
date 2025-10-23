package com.wms.goals.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectionPoint {
    private int month;              // elapsed months from start
    private String date;            // ISO date at period start
    private BigDecimal value;       // accumulated value of investment at this point
    private double progress;        // 0..1 of target value
}

