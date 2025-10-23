package com.wms.goals.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOtherGoalRequest {
    @JsonProperty("goalType")
    @NotBlank private String goalType;
    @JsonProperty("goalName")
    @NotBlank private String goalName;
    @JsonProperty("targetYear")
    @NotNull @Min(1900) private Integer targetYear;
    @JsonProperty("targetAmount")
    @NotNull @Positive private BigDecimal targetAmount;
}
