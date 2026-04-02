package com.tmdt.m3_pj_final_namqd.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CriterionWeightResponse {
    private Long criterionId;
    private String criterionName;
    private Double weight;
}
