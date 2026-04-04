package com.tmdt.m3_pj_final_namqd.controller;

import com.tmdt.m3_pj_final_namqd.dto.request.round_criteria.RoundCriteriaRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import com.tmdt.m3_pj_final_namqd.service.RoundCriteriaService;
import com.tmdt.m3_pj_final_namqd.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/round_criteria")
@RequiredArgsConstructor
@Tag(name = "08. Round criteria", description = "Tiêu chí trong đợt đánh giá — xem: ADMIN, MENTOR, STUDENT; thêm/sửa trọng số/xóa: ADMIN")
public class RoundCriteriaController {

    private final RoundCriteriaService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    @Operation(summary = "Danh sách tiêu chí theo roundId")
    public ResponseEntity<ApiResponse<?>> getByRound(@RequestParam Long roundId) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.getByRound(roundId),
                        "Lấy danh sách tiêu chí trong đợt đánh giá thành công"
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    @Operation(summary = "Chi tiết round_criterion")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.getById(id),
                        "Lấy chi tiết tiêu chí trong đợt đánh giá thành công"
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Thêm tiêu chí vào đợt (trọng số)")
    public ResponseEntity<ApiResponse<?>> create(@RequestBody RoundCriteriaRequest request) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.create(request),
                        "Thêm tiêu chí vào đợt đánh giá thành công"
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật trọng số tiêu chí trong đợt")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id,
                                                 @RequestBody RoundCriteriaRequest request) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.update(id, request),
                        "Cập nhật tiêu chí trong đợt đánh giá thành công"
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Gỡ tiêu chí khỏi đợt")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.ok(
                ResponseUtil.success(
                        null,
                        "Xóa tiêu chí khỏi đợt đánh giá thành công"
                )
        );
    }
}
