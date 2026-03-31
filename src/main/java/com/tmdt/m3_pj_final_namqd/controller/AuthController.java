package com.tmdt.m3_pj_final_namqd.controller;

import com.tmdt.m3_pj_final_namqd.config.jwt.JwtProvider;
import com.tmdt.m3_pj_final_namqd.dto.request.LoginRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import com.tmdt.m3_pj_final_namqd.dto.response.UserResponse;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.service.AuthService;
import com.tmdt.m3_pj_final_namqd.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "1. Authentication", description = "APIs for login & security")
public class AuthController {
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public AuthController(JwtProvider jwtProvider, PasswordEncoder passwordEncoder, AuthService authService) {
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }


    @PostMapping("/login")
    public ApiResponse<String> login(@RequestBody LoginRequest request) {
        return ResponseUtil.success(
                authService.login(request),
                "Đăng nhập thành công"
        );
    }
    @GetMapping("/me")
    @Operation(summary = "Get profile")
    public ApiResponse<UserResponse> getCurrentUser(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AppException("Unauthorized");
        }

        User user = (User) authentication.getPrincipal();

        return ResponseUtil.success(
                authService.getCurrentUser(user),
                "Lấy thông tin user thành công"
        );
    }

}
