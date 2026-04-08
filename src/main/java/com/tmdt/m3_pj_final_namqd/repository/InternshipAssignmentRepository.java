package com.tmdt.m3_pj_final_namqd.repository;

import com.tmdt.m3_pj_final_namqd.entity.AssignmentStatus;
import com.tmdt.m3_pj_final_namqd.entity.InternshipAssignment;
import com.tmdt.m3_pj_final_namqd.entity.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

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

    List<InternshipAssignment> findByStudent_Id(Long studentId);

    List<InternshipAssignment> findByMentor_Id(Long mentorId);

    boolean existsByStudent_IdAndPhase_Id(Long studentId, Long phaseId);

    @Query("""
            select distinct ia.student
            from InternshipAssignment ia
            where (ia.mentor.user.id = :mentorUserId or ia.mentor.id = :mentorUserId)
            """)
    List<Student> findAssignedStudentsByMentorUserId(
            @Param("mentorUserId") Long mentorUserId
    );

    @Query("""
            select (count(ia) > 0)
            from InternshipAssignment ia
            where (ia.mentor.user.id = :mentorUserId or ia.mentor.id = :mentorUserId)
              and (ia.student.id = :studentId or ia.student.user.id = :studentId)
            """)
    boolean existsAssignedStudentForMentor(
            @Param("mentorUserId") Long mentorUserId,
            @Param("studentId") Long studentId
    );

}
