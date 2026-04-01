package com.tmdt.m3_pj_final_namqd.dto.request.user;

import com.tmdt.m3_pj_final_namqd.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "username không được để trống")
    private String username;
    @Size(min = 3, message = "Mật khẩu tối thiểu 3 ký tự")
    private String password;
    @Email(message = "Email không hợp lệ")
    private String email;
    private String fullName;
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng")
    private String phoneNumber;
    private Role role;
}
