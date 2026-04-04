package com.tmdt.m3_pj_final_namqd.controller;

import com.tmdt.m3_pj_final_namqd.dto.request.assessment_round.AssessmentRoundRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import com.tmdt.m3_pj_final_namqd.dto.response.AssessmentRoundResponse;
import com.tmdt.m3_pj_final_namqd.service.AssessmentRoundService;
import com.tmdt.m3_pj_final_namqd.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessment_rounds")
@RequiredArgsConstructor
@Tag(name = "07. Assessment rounds", description = "Xem (lọc phase_id): ADMIN, MENTOR, STUDENT; CRUD: ADMIN")
public class AssessmentRoundController {
    private final AssessmentRoundService service;

    // get all
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    @Operation(summary = "Danh sách đợt đánh giá (phaseId tùy chọn)")
    public ResponseEntity<ApiResponse<List<AssessmentRoundResponse>>> getAll(
            @RequestParam(required = false) Long phaseId
    ) {
        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.getAll(phaseId),
                        "Lấy danh sách đợt đánh giá thành công"
                )
        );
    }

    // detail
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    @Operation(summary = "Chi tiết đợt đánh giá")
    public ResponseEntity<ApiResponse<AssessmentRoundResponse>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.getById(id),
                        "Lấy chi tiết đợt đánh giá thành công"
                )
        );
    }

    // create
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo đợt đánh giá (kèm tiêu chí + trọng số)")
    public ResponseEntity<ApiResponse<AssessmentRoundResponse>> create(
            @Valid @RequestBody AssessmentRoundRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ResponseUtil.success(
                                service.create(request),
                                "Tạo đợt đánh giá thành công"
                        )
                );
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật đợt đánh giá")
    public ResponseEntity<ApiResponse<AssessmentRoundResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AssessmentRoundRequest request
    ) {
        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.update(id, request),
                        "Cập nhật đợt đánh giá thành công"
                )
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa đợt đánh giá")
    public ResponseEntity<ApiResponse<?>> delete(
            @PathVariable Long id
    ) {
        service.delete(id);

        return ResponseEntity.ok(
                ResponseUtil.success(null, "Xóa đợt đánh giá thành công")
        );
    }
}
