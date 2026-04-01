package com.tmdt.m3_pj_final_namqd.repository;

import com.tmdt.m3_pj_final_namqd.entity.InternshipPhase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternshipPhaseRepository extends JpaRepository<InternshipPhase, Long> {

    boolean existsByPhaseName(String phaseName);
}
