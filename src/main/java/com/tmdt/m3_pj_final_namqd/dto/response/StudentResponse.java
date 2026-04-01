package com.tmdt.m3_pj_final_namqd.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class StudentResponse {

    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String studentCode;
    private String major;
    private String className;
    private LocalDate dateOfBirth;
    private String address;
}
