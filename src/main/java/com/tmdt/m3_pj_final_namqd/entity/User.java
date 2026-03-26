package com.tmdt.m3_pj_final_namqd.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@Entity
@Table(name = "users")
@Getter
@Setter
@SQLDelete(sql = "UPDATE users SET is_deleted = true, deleted_at = now() WHERE id = ?")
@SQLRestriction("is_deleted = false")
public class User extends BaseEntity{
    @Column(unique = true, nullable = false)
    private String username;

    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(name = "phone_number", length = 10)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;
}
