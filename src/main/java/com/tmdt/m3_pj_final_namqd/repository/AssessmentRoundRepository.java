package com.tmdt.m3_pj_final_namqd.repository;

import com.tmdt.m3_pj_final_namqd.entity.AssessmentRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssessmentRoundRepository extends JpaRepository<AssessmentRound, Long> {

    List<AssessmentRound> findByPhase_Id(Long phaseId);
    boolean existsByPhase_Id(Long phaseId);
}
