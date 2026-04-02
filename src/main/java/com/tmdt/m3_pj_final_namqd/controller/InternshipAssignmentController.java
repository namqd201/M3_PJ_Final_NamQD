package com.tmdt.m3_pj_final_namqd.controller;

import com.tmdt.m3_pj_final_namqd.dto.request.internship_assignment.InternshipAssignmentRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.internship_assignment.UpdateAssignmentStatusRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.service.InternshipAssignmentService;
import com.tmdt.m3_pj_final_namqd.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internship_assignments")
@RequiredArgsConstructor
public class InternshipAssignmentController {

    private final InternshipAssignmentService service;

    // list
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAll(Authentication auth) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.getAll((User) auth.getPrincipal()),
                        "Lấy danh sách phân công thành công"
                )
        );
    }

    // ================= GET DETAIL =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id,
                                                  Authentication auth) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.getById(id, (User) auth.getPrincipal()),
                        "Lấy chi tiết phân công thành công"
                )
        );
    }

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@RequestBody InternshipAssignmentRequest request,
                                                 Authentication auth) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.create(request, (User) auth.getPrincipal()),
                        "Tạo phân công thành công"
                )
        );
    }

    // ================= UPDATE STATUS =================
    @PutMapping("/{id}/status")
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
