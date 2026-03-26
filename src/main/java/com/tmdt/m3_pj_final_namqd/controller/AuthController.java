package com.tmdt.m3_pj_final_namqd.controller;

import com.tmdt.m3_pj_final_namqd.config.jwt.JwtProvider;
import com.tmdt.m3_pj_final_namqd.dto.request.LoginRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "APIs for login & security")
public class AuthController {
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        // TODO: sau này lấy từ DB
        if ("admin".equals(request.username) &&
                "123456".equals(request.password)) {

            return jwtProvider.generateToken(request.username);
        }

        throw new RuntimeException("Sai tài khoản hoặc mật khẩu");
    }
}
