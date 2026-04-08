package com.tmdt.m3_pj_final_namqd.repository;

import com.tmdt.m3_pj_final_namqd.entity.RoundCriteria;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoundCriteriaRepository extends JpaRepository<RoundCriteria, Long> {

    List<RoundCriteria> findByAssessmentRound_Id(Long roundId);

    void deleteByAssessmentRound_Id(Long roundId);

    @Modifying
    @Query("delete from RoundCriteria rc where rc.assessmentRound.id = :roundId")
    void hardDeleteByAssessmentRoundId(@Param("roundId") Long roundId);

    boolean existsByEvaluationCriteria_Id(Long criterionId);

    boolean existsByAssessmentRound_IdAndEvaluationCriteria_Id(Long roundId, Long criteriaId);
}