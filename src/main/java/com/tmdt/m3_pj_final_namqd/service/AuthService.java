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
import java.util.HashMap;
import java.util.Map;

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
                    Map<String, String> errors = new HashMap<>();
                    errors.put("username", "Username not found");
                    return new AppException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, errors);
                });

        if (!user.getIsActive()) {
            log.warn("Login failed: inactive user '{}'", username);
            Map<String, String> errors = new HashMap<>();
            errors.put("user", "User is disabled");
            throw new AppException("USER_DISABLED", HttpStatus.FORBIDDEN, errors);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Login failed: wrong password for user '{}'", username);
            Map<String, String> errors = new HashMap<>();
            errors.put("password", "Incorrect password");
            throw new AppException("INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED,  errors);
        }

        return jwtProvider.generateToken(user.getUsername());
    }

    public String register(RegisterRequest request) {

        // check trùng
        if (userRepository.existsByUsernameAndIsDeletedFalse(request.getUsername())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("username", "Username already exists");
            throw new AppException("USERNAME_ALREADY_EXISTS", HttpStatus.CONFLICT, errors);
        }

        if (userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "Email already exists");
            throw new AppException("EMAIL_ALREADY_EXISTS", HttpStatus.CONFLICT, errors);
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
                .isActive(user.getIsActive())
                .build();
    }
}
