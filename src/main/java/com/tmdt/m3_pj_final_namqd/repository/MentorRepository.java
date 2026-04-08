package com.tmdt.m3_pj_final_namqd.repository;

import com.tmdt.m3_pj_final_namqd.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    Optional<Mentor> findByUser_Id(Long userId);

    //đếm số lượng mentor theo phòng ban
    @Query(value = "select count(m.id) from mentors m where m.department = :department",  nativeQuery = true)
    int countMentorByDepartment(@Param("department") String department);
}
