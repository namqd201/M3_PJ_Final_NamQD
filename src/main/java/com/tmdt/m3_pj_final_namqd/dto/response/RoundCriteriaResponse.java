package com.tmdt.m3_pj_final_namqd.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoundCriteriaResponse {
    private Long id;
    private Long roundId;
    private String roundName;
    private Long criteriaId;
    private String criteriaName;
    private Double weight;
}
