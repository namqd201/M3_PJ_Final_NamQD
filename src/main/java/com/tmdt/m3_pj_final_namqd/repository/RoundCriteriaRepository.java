package com.tmdt.m3_pj_final_namqd.repository;

import com.tmdt.m3_pj_final_namqd.entity.RoundCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoundCriteriaRepository extends JpaRepository<RoundCriteria, Long> {

    List<RoundCriteria> findByAssessmentRoundId(Long roundId);

    void deleteByAssessmentRoundId(Long roundId);

    boolean existsByEvaluationCriteriaId(Long criterionId);

    boolean existsByAssessmentRoundIdAndEvaluationCriteriaId(Long roundId, Long criteriaId);
}