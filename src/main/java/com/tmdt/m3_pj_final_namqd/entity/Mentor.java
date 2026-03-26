package com.tmdt.m3_pj_final_namqd.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "mentors")
public class Mentor extends BaseEntity{

    @OneToOne
    @MapsId // Lấy ID của User làm ID của Student
    @JoinColumn(name = "id")
    private User user;

    private String department;

    @Column(name = "academic_rank")
    private String academicRank;
}
