package com.wms.goals.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EditOtherGoalRequest {
    @JsonProperty("targetYear")
    @NotNull @Min(1900) private Integer targetYear;
    @JsonProperty("targetAmount")
    @NotNull @Positive private BigDecimal targetAmount;
}
