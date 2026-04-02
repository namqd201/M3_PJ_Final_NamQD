package com.tmdt.m3_pj_final_namqd.dto.request.internship_assignment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternshipAssignmentRequest {

    private Long studentId;
    private Long mentorId;
    private Long phaseId;
}
