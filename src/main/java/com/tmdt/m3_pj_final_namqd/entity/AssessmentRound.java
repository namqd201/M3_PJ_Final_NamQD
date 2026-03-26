package com.tmdt.m3_pj_final_namqd.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "assessment_rounds")
@Getter
@Setter
@SQLDelete(sql = "UPDATE assessment_rounds SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class AssessmentRound extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "phase_id", nullable = false)
    private InternshipPhase phase;

    @Column(name = "round_name", nullable = false)
    private String roundName;

    private LocalDate startDate;
    private LocalDate endDate;

    private String description;

    private boolean isActive = true;
}
