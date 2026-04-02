package com.tmdt.m3_pj_final_namqd.dto.request.assessment_result;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentResultUpdateRequest {

    @NotNull
    private Double score;

    private String comments;
}
