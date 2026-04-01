package com.tmdt.m3_pj_final_namqd.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvaluationCriteriaResponse {

    private Long id;
    private String criterionName;
    private Double maxScore;
    private String description;
}