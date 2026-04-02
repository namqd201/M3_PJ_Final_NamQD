package com.tmdt.m3_pj_final_namqd.dto.request.round_criteria;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoundCriteriaRequest {
    private Long roundId;
    private Long criteriaId;
    private Double weight;
}
