package com.tmdt.m3_pj_final_namqd.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignMentorRequest {

    @NotNull
    private Long studentId;

    @NotNull
    private Long mentorId;

    @NotNull
    private Long phaseId;
}
