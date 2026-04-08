package com.tmdt.m3_pj_final_namqd.repository;

import com.tmdt.m3_pj_final_namqd.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByStudentCode(String studentCode);
    boolean existsById(Long id);
    Optional<Student> findByUser_Id(Long userId);
}
