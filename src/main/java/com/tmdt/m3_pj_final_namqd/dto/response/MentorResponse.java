package com.tmdt.m3_pj_final_namqd.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MentorResponse {

    private Long id;

    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;

    private String department;
    private String academicRank;
}
