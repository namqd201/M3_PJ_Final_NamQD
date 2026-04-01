package com.tmdt.m3_pj_final_namqd.dto.request.student;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateStudentRequest {

    @NotNull(message = "UserId không được để trống")
    private Long userId;

    @NotBlank(message = "Mã sinh viên không được để trống")
    private String studentCode;

    private String major;

    private String className;

    private LocalDate dateOfBirth;

    private String address;
}
