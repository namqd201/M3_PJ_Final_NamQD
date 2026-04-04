package com.tmdt.m3_pj_final_namqd.controller;

import com.tmdt.m3_pj_final_namqd.dto.request.internship_assignment.InternshipAssignmentRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.internship_assignment.UpdateAssignmentStatusRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.service.InternshipAssignmentService;
import com.tmdt.m3_pj_final_namqd.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internship_assignments")
@RequiredArgsConstructor
@Tag(name = "09. Internship assignments", description = "Phân công — xem: ADMIN, MENTOR, STUDENT (lọc theo quyền); tạo & cập nhật trạng thái: ADMIN")
public class InternshipAssignmentController {

    private final InternshipAssignmentService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    @Operation(summary = "Danh sách phân công (theo vai trò / user)")
    public ResponseEntity<ApiResponse<?>> getAll(Authentication auth) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.getAll((User) auth.getPrincipal()),
                        "Lấy danh sách phân công thành công"
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR','STUDENT')")
    @Operation(summary = "Chi tiết phân công (theo vai trò)")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id,
                                                  Authentication auth) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.getById(id, (User) auth.getPrincipal()),
                        "Lấy chi tiết phân công thành công"
                )
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo phân công (sinh viên — mentor — đợt)")
    public ResponseEntity<ApiResponse<?>> create(@RequestBody InternshipAssignmentRequest request,
                                                 Authentication auth) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.create(request, (User) auth.getPrincipal()),
                        "Tạo phân công thành công"
                )
        );
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật trạng thái phân công")
    public ResponseEntity<ApiResponse<?>> updateStatus(@PathVariable Long id,
                                                       @RequestBody UpdateAssignmentStatusRequest request,
                                                       Authentication auth) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.updateStatus(id, request.getStatus(), (User) auth.getPrincipal()),
                        "Cập nhật trạng thái thành công"
                )
        );
    }
}
