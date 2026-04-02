package com.tmdt.m3_pj_final_namqd.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "round_criteria", uniqueConstraints = @UniqueConstraint(columnNames = {"round_id", "criterion_id"}))
@Getter
@Setter
@SQLDelete(sql = "UPDATE round_criteria SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class RoundCriteria extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "round_id", nullable = false)
    private AssessmentRound assessmentRound;

    @ManyToOne
    @JoinColumn(name = "criterion_id", nullable = false)
    private EvaluationCriteria evaluationCriteria;

    @Column(nullable = false)
    private Double weight;

    @PrePersist
    @PreUpdate
    private void validateWeight() {
        if (weight == null || weight <= 0) {
            throw new IllegalArgumentException("Weight must be > 0");
        }
    }
}