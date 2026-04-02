package com.tmdt.m3_pj_final_namqd.dto.request.assessment_round;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AssessmentRoundRequest {
    private Long phaseId;
    private String roundName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Boolean isActive;

    private List<CriterionWeightRequest> criteria;
}

