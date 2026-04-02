package com.tmdt.m3_pj_final_namqd.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InternshipAssignmentResponse {

    private Long id;

    private Long studentId;
    private String studentName;

    private Long mentorId;
    private String mentorName;

    private Long phaseId;
    private String phaseName;

    private String status;
}
