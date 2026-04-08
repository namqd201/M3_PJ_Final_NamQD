package com.tmdt.m3_pj_final_namqd.repository;

import com.tmdt.m3_pj_final_namqd.entity.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {

    boolean existsByAssessmentRound_Id(Long roundId);

    boolean existsByAssessmentRound_IdAndCriterion_Id(Long roundId, Long criterionId);
    boolean existsByCriterion_Id(Long criterionId);

    boolean existsByAssignment_IdAndAssessmentRound_IdAndCriterion_Id(
            Long assignmentId,
            Long roundId,
            Long criterionId
    );

    List<AssessmentResult> findByAssignment_Id(Long assignmentId);

    List<AssessmentResult> findByAssignment_Mentor_Id(Long mentorId);

    List<AssessmentResult> findByAssignment_Student_Id(Long studentId);
}
