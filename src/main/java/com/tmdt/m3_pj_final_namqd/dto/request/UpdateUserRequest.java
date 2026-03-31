package com.tmdt.m3_pj_final_namqd.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String username;
    private String email;
    private String fullName;
    private String role;
}
