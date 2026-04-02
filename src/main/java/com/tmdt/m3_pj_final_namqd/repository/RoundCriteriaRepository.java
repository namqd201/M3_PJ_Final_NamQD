package com.tmdt.m3_pj_final_namqd.repository;

import com.tmdt.m3_pj_final_namqd.entity.RoundCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoundCriteriaRepository extends JpaRepository<RoundCriteria, Long> {

    List<RoundCriteria> findByAssessmentRound_Id(Long roundId);

    void deleteByAssessmentRound_Id(Long roundId);

    boolean existsByEvaluationCriteria_Id(Long criterionId);

    boolean existsByAssessmentRound_IdAndEvaluationCriteria_Id(Long roundId, Long criteriaId);
}