package com.tmdt.m3_pj_final_namqd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "evaluation_criteria")
@Getter
@Setter
@SQLDelete(sql = "UPDATE evaluation_criteria SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class EvaluationCriteria extends BaseEntity {

    @Column(name = "criterion_name", unique = true, nullable = false)
    private String criterionName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_score", nullable = false)
    private Double maxScore;
}