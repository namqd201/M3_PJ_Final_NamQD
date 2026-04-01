package com.tmdt.m3_pj_final_namqd.repository;

import com.tmdt.m3_pj_final_namqd.entity.AssignmentStatus;
import com.tmdt.m3_pj_final_namqd.entity.InternshipAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipAssignmentRepository extends JpaRepository<InternshipAssignment, Long> {

    List<InternshipAssignment> findByMentor_User_Id(Long mentorUserId);

    List<InternshipAssignment> findByMentor_User_IdAndStatus(
            Long mentorUserId,
            AssignmentStatus status
    );

    boolean existsByMentor_User_IdAndStudent_IdAndStatus(
            Long mentorId,
            Long studentId,
            AssignmentStatus status
    );

}
