package com.tmdt.m3_pj_final_namqd.service;

import com.tmdt.m3_pj_final_namqd.config.jwt.JwtProvider;
import com.tmdt.m3_pj_final_namqd.dto.request.LoginRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.user.RegisterRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.UserResponse;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public String login(LoginRequest request) {

        String username = request.getUsername();
        User user = userRepository
                .findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> {
                    log.warn("Login failed: unknown username '{}'", username);
                    return new AppException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED);
                });

        if (!user.getIsActive()) {
            log.warn("Login failed: inactive user '{}'", username);
            throw new AppException("USER_DISABLED", HttpStatus.FORBIDDEN);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Login failed: wrong password for user '{}'", username);
            throw new AppException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED);
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
