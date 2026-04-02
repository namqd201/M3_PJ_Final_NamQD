package com.tmdt.m3_pj_final_namqd.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AssessmentResultResponse {

    private Long id;

    private Long assignmentId;
    private Long studentId;
    private String studentName;
    private Long mentorId;
    private String mentorName;

    private Long phaseId;
    private String phaseName;

    private Long roundId;
    private String roundName;

    private Long criterionId;
    private String criterionName;

    private Double score;
    private String comments;

    private Long evaluatorId;
    private String evaluatorName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime evaluationDate;
}
