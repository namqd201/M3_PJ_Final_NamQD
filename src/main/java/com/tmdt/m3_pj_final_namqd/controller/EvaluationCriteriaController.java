package com.tmdt.m3_pj_final_namqd.controller;

import com.tmdt.m3_pj_final_namqd.dto.request.EvaluationCriteriaRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import com.tmdt.m3_pj_final_namqd.dto.response.EvaluationCriteriaResponse;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.UserRepository;
import com.tmdt.m3_pj_final_namqd.service.EvaluationCriteriaService;
import com.tmdt.m3_pj_final_namqd.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluation_criteria")
@RequiredArgsConstructor
@Tag(name = "06. Evaluation criteria", description = "Xem: ADMIN, MENTOR, STUDENT; tạo/sửa/xóa: ADMIN")
public class EvaluationCriteriaController {

    private final EvaluationCriteriaService service;
    private final UserRepository userRepository;

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByUsernameAndIsDeletedFalse(auth.getName())
                .orElseThrow(() -> new AppException("User không tồn tại", HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    @Operation(summary = "Danh sách tiêu chí đánh giá")
    public ResponseEntity<ApiResponse<List<EvaluationCriteriaResponse>>> getAll() {
        return ResponseEntity.ok(
                ResponseUtil.success(service.getAll(), "Lấy danh sách thành công")
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    @Operation(summary = "Chi tiết tiêu chí")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ResponseUtil.success(service.getById(id), "Lấy chi tiết thành công")
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo tiêu chí")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> create(
            @Valid @RequestBody EvaluationCriteriaRequest request,
            Authentication auth
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        service.create(request, getCurrentUser(auth)),
                        "Tạo tiêu chí thành công"
                ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật tiêu chí")
    public ResponseEntity<ApiResponse<EvaluationCriteriaResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody EvaluationCriteriaRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.update(id, request, getCurrentUser(auth)),
                        "Cập nhật tiêu chí thành công"
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa tiêu chí")
    public ResponseEntity<ApiResponse<?>> delete(
            @PathVariable Long id,
            Authentication auth
    ) {

        service.delete(id, getCurrentUser(auth));

        return ResponseEntity.ok(
                ResponseUtil.success(null, "Xóa tiêu chí thành công")
        );
    }
}
