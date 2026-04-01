package com.tmdt.m3_pj_final_namqd.service;

import com.tmdt.m3_pj_final_namqd.dto.request.user.UpdateUserRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.user.UpdateUserRoleRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.user.UpdateUserStatusRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.UserResponse;
import com.tmdt.m3_pj_final_namqd.entity.Role;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Get all
    public List<UserResponse> getAllUsers(String role) {

        List<User> users;

        if (role == null || role.isBlank()) {
            users = userRepository.findAll();
        } else {
            try {
                Role roleEnum = Role.valueOf(role.toUpperCase());
                users = userRepository.findByRoleAndIsDeletedFalse(roleEnum);
            } catch (Exception e) {
                throw new AppException("INVALID_ROLE", HttpStatus.BAD_REQUEST);
            }
        }

        return users.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    public UserResponse getUserById(Long userId) {

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AppException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new AppException("USER_IS_INACTIVE", HttpStatus.FORBIDDEN);
        }

        return mapToResponse(user);
    }

    //Update
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AppException("USER_NOT_FOUND",  HttpStatus.NOT_FOUND));


        // update từng field (không update null)
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }


        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return mapToResponse(user);
    }

    // Update Status
    public UserResponse updateUserStatus(Long userId, UpdateUserStatusRequest request) {

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AppException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));


        if (request.getIsActive() == null) {
            throw new AppException("INVALID_REQUEST", HttpStatus.BAD_REQUEST);
        }


        user.setIsActive(request.getIsActive());

        userRepository.save(user);

        return mapToResponse(user);
    }

    public UserResponse updateUserRole(Long userId,
                                       UpdateUserRoleRequest request,
                                       User currentUser) {

        User targetUser = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AppException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));


        // validate request
        if (request.getRole() == null || request.getRole().isBlank()) {
            throw new AppException("INVALID_REQUEST", HttpStatus.BAD_REQUEST);
        }

        Role newRole;
        try {
            newRole = Role.valueOf(request.getRole().toUpperCase());
        } catch (Exception e) {
            throw new AppException("INVALID_REQUEST", HttpStatus.BAD_REQUEST);
        }

        // ADMIN không được sửa role của ADMIN khác
        if (targetUser.getRole() == Role.ADMIN && currentUser.getId() != targetUser.getId()) {
            throw new AppException("FORBIDDEN_ACTION", HttpStatus.FORBIDDEN);
        }

        // không cho tự downgrade chính mình
        if (currentUser.getId().equals(targetUser.getId()) && newRole != Role.ADMIN) {
            throw new AppException("FORBIDDEN_ACTION", HttpStatus.FORBIDDEN);
        }

        targetUser.setRole(newRole);

        userRepository.save(targetUser);

        return mapToResponse(targetUser);
    }

    public void deleteUser(Long userId, User currentUser) {

        User targetUser = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new AppException("USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        // không cho xóa chính mình
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new AppException("FORBIDDEN_ACTION", HttpStatus.FORBIDDEN);
        }

        // không cho xóa ADMIN khác
        if (targetUser.getRole() == Role.ADMIN) {
            throw new AppException("FORBIDDEN_ACTION", HttpStatus.FORBIDDEN);
        }

        // soft delete
        targetUser.setDeleted(true);
        targetUser.setDeletedAt(LocalDateTime.now());

        userRepository.save(targetUser);
    }
}
