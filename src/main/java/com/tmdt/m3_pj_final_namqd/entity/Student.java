package com.tmdt.m3_pj_final_namqd.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Date;

@Entity
@Table(name = "students")
@Getter
@Setter
@SQLDelete(sql = "UPDATE students SET is_deleted = true, deleted_at = NOW() WHERE id=?")
@SQLRestriction("is_deleted = false")
public class Student extends BaseEntity {

    @OneToOne
    @MapsId // Lấy ID của User làm ID của Student
    @JoinColumn(name = "id")
    private User user;

    @Column(unique = true, name = "student_code")
    private String studentCode;

    private String major;

    @Column(name = "class_name")
    private String className;

    private Date dateOfBirth;

    private String address;
}
