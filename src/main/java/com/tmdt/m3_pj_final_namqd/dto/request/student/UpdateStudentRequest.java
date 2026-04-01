package com.tmdt.m3_pj_final_namqd.dto.request.student;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateStudentRequest {

    private String major;

    private String className;

    private LocalDate dateOfBirth;

    private String address;
}