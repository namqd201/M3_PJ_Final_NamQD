package com.tmdt.m3_pj_final_namqd.service;

import com.tmdt.m3_pj_final_namqd.config.jwt.JwtProvider;
import com.tmdt.m3_pj_final_namqd.dto.request.LoginRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.RegisterRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.UserResponse;
import com.tmdt.m3_pj_final_namqd.entity.Role;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public String login(LoginRequest request) {

        User user = userRepository.findByUsernameAndIsDeletedFalse(request.getUsername())
                .orElseThrow(() -> new AppException("USER_NOT_FOUND"));

        if (!user.getIsActive()) {
            throw new AppException("USER_DISABLED");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException("INCORRECT_PASSWORD");
        }

        return jwtProvider.generateToken(user.getUsername());
    }

    public String register(RegisterRequest request) {

        // check trùng
        if (userRepository.existsByUsernameAndIsDeletedFalse(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // tạo user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setDeleted(false);

        userRepository.save(user);

        return "Register success";
    }

    public UserResponse getCurrentUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}
