package com.tmdt.m3_pj_final_namqd.dto.request.mentor;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMentorRequest {

    @NotNull
    private Long userId;

    private String department;
    private String academicRank;
}
