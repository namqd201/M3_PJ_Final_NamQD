package com.tmdt.m3_pj_final_namqd.repository;

import com.tmdt.m3_pj_final_namqd.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    Optional<Mentor> findByUser_Id(Long userId);
}
