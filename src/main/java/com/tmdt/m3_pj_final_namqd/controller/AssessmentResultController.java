package com.tmdt.m3_pj_final_namqd.controller;

import com.tmdt.m3_pj_final_namqd.dto.request.assessment_result.AssessmentResultRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.assessment_result.AssessmentResultUpdateRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import com.tmdt.m3_pj_final_namqd.dto.response.AssessmentResultResponse;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.UserRepository;
import com.tmdt.m3_pj_final_namqd.service.AssessmentResultService;
import com.tmdt.m3_pj_final_namqd.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessment_results")
@RequiredArgsConstructor
@Tag(name = "8. Assessment Results", description = "APIs for assessment results (scores)")
public class AssessmentResultController {

    private final AssessmentResultService service;
    private final UserRepository userRepository;

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByUsernameAndIsDeletedFalse(auth.getName())
                .orElseThrow(() -> new AppException("User không tồn tại", HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @Operation(summary = "Danh sách kết quả đánh giá (theo quyền, lọc assignment_id, user_id)")
    public ResponseEntity<ApiResponse<List<AssessmentResultResponse>>> getAll(
            @RequestParam(required = false) Long assignmentId,
            @RequestParam(required = false) Long userId,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.getAll(currentUser, assignmentId, userId),
                        "Lấy danh sách kết quả đánh giá thành công"
                )
        );
    }

    @PostMapping
    @Operation(summary = "Tạo kết quả đánh giá (mentor được phân công)")
    public ResponseEntity<ApiResponse<AssessmentResultResponse>> create(
            @Valid @RequestBody AssessmentResultRequest request,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ResponseUtil.success(
                                service.create(request, currentUser),
                                "Tạo kết quả đánh giá thành công"
                        )
                );
    }

    @PutMapping("/{resultId}")
    @Operation(summary = "Cập nhật kết quả đánh giá do chính mentor tạo")
    public ResponseEntity<ApiResponse<AssessmentResultResponse>> update(
            @PathVariable Long resultId,
            @Valid @RequestBody AssessmentResultUpdateRequest request,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.update(resultId, request, currentUser),
                        "Cập nhật kết quả đánh giá thành công"
                )
        );
    }
}
