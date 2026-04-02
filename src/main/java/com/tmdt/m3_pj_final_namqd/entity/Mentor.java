package com.tmdt.m3_pj_final_namqd.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "mentors")
public class Mentor extends BaseEntity{

    @Id
    private Long id;

    @OneToOne
    @MapsId // Lấy ID của User làm ID của Mentor
    @JoinColumn(name = "id")
    private User user;

    private String department;

    @Column(name = "academic_rank")
    private String academicRank;

    @OneToMany(mappedBy = "mentor")
    private List<InternshipAssignment> assignments;
}
