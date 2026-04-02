package com.tmdt.m3_pj_final_namqd.repository;

import com.tmdt.m3_pj_final_namqd.entity.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {

    boolean existsByAssessmentRoundId(Long roundId);

    boolean existsByRoundCriteriaId(Long roundCriteriaId);
}
