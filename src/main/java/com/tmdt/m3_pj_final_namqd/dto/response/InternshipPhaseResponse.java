package com.tmdt.m3_pj_final_namqd.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class InternshipPhaseResponse {
    private Long id;
    private String phaseName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
