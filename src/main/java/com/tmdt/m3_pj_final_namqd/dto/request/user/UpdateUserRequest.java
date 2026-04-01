package com.tmdt.m3_pj_final_namqd.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    @NotBlank(message = "username không được để trống")
    private String username;
    @Email(message = "Email không hợp lệ")
    private String email;
    private String fullName;
    private String role;
}
