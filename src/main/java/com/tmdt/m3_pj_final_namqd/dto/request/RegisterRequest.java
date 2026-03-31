package com.tmdt.m3_pj_final_namqd.dto.request;

import com.tmdt.m3_pj_final_namqd.entity.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Role role;
}
