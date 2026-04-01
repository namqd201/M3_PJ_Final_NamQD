package com.tmdt.m3_pj_final_namqd.dto.request.internship_phase;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InternshipPhaseRequest {

    @NotNull(message = "Tên giai đoạn không được để trống")
    private String phaseName;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    private String description;
}
