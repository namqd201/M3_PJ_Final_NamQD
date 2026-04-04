package com.tmdt.m3_pj_final_namqd.controller;

import com.tmdt.m3_pj_final_namqd.dto.request.user.RegisterRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.user.UpdateUserRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.user.UpdateUserRoleRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.user.UpdateUserStatusRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import com.tmdt.m3_pj_final_namqd.dto.response.UserResponse;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.service.AuthService;
import com.tmdt.m3_pj_final_namqd.service.UserService;
import com.tmdt.m3_pj_final_namqd.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "02. User management", description = "Quản lý user — toàn bộ endpoint chỉ ADMIN")
public class UserController {
    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin tạo tài khoản mới")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        authService.register(request),
                        "Đăng ký thành công"
                ));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin lấy danh sách tài khoản")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String role
    ) {
        return ResponseEntity.ok(
                ResponseUtil.success(
                userService.getAllUsers(role),
                "Lấy danh sách user thành công")
        );
    }

    @GetMapping("/{user_id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin lấy thông tin tài khoản theo id")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long user_id) {
        return ResponseEntity.ok(ResponseUtil.success(
                userService.getUserById(user_id),
                "Lấy chi tiết user thành công")
        );
    }

    @PutMapping("/{user_id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin cập nhật thông tin người dùng")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Valid
            @PathVariable Long user_id,
            @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                userService.updateUser(user_id, request),
                "Cập nhật người dùng thành công")
        );
    }

    // Theo như trong tài liệu yêu cầu thì dùng put, nhưng vì ở đây chỉ update 1 file thì nên dùng patch
    @PatchMapping("/{user_id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin cập nhật trạng thái kích hoạt người dùng")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable("user_id") Long userId,
            @RequestBody UpdateUserStatusRequest request) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                userService.updateUserStatus(userId, request),
                "Cập nhật trạng thái thành công")
        );
    }

    // update role
    @PatchMapping("/{user_id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin cập nhật vai trò người dùng")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable("user_id") Long userId,
            @RequestBody UpdateUserRoleRequest request,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                ResponseUtil.success(
                userService.updateUserRole(userId, request, currentUser),
                "Cập nhật role thành công")
        );
    }

    // delete user
    @DeleteMapping("/{user_id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin xóa mềm người dùng")
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @PathVariable("user_id") Long userId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();

        userService.deleteUser(userId, currentUser);

        return ResponseEntity.ok(
                ResponseUtil.success(
                null,
                "Xóa người dùng thành công")
        );
    }
}
