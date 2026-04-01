package com.tmdt.m3_pj_final_namqd.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EvaluationCriteriaRequest {

    @NotNull(message = "Tên tiêu chí không được để trống")
    private String criterionName;

    @NotNull(message = "Max score không được để trống")
    @Min(value = 0, message = "Max score phải >= 0")
    private Double maxScore;

    private String description;
}
