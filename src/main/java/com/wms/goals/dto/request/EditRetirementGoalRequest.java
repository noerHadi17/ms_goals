package com.wms.goals.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EditRetirementGoalRequest {
    @JsonProperty("targetAge")
    @NotNull @Min(1) @Max(80) private Integer targetAge;
    @JsonProperty("hopeLife")
    @NotNull @Min(1) @Max(60) private Integer yearsAfterRetirement;
    @JsonProperty("monthlyExpense")
    @NotNull @Positive private BigDecimal monthlyExpense;
}
