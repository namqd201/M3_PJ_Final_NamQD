package com.tmdt.m3_pj_final_namqd.controller;

import com.tmdt.m3_pj_final_namqd.dto.request.internship_phase.InternshipPhaseRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import com.tmdt.m3_pj_final_namqd.dto.response.InternshipPhaseResponse;
import com.tmdt.m3_pj_final_namqd.service.InternshipPhaseService;
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
@RequestMapping("/api/internship_phases")
@RequiredArgsConstructor
@Tag(name = "05. Internship phases", description = "Xem: ADMIN, MENTOR, STUDENT; tạo/sửa/xóa: ADMIN")
public class InternshipPhaseController {
    private final InternshipPhaseService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    @Operation(summary = "Danh sách đợt thực tập")
    public ResponseEntity<ApiResponse<List<InternshipPhaseResponse>>> getAll() {
        return ResponseEntity.ok(
                ResponseUtil.success(service.getAll(), "Lấy danh sách thành công")
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    @Operation(summary = "Chi tiết đợt thực tập")
    public ResponseEntity<ApiResponse<InternshipPhaseResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ResponseUtil.success(service.getById(id), "Lấy chi tiết thành công")
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo đợt thực tập")
    public ResponseEntity<ApiResponse<InternshipPhaseResponse>> create(
            @Valid @RequestBody InternshipPhaseRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseUtil.success(
                        service.create(request),
                        "Tạo thành công"
                ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật đợt thực tập")
    public ResponseEntity<ApiResponse<InternshipPhaseResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody InternshipPhaseRequest request
    ) {
        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.update(id, request),
                        "Cập nhật thành công"
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa đợt thực tập")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(
                ResponseUtil.success(null, "Xóa thành công")
        );
    }
}
