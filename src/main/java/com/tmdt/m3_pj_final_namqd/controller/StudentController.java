package com.tmdt.m3_pj_final_namqd.controller;

import com.tmdt.m3_pj_final_namqd.dto.request.student.CreateStudentRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.student.UpdateStudentRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import com.tmdt.m3_pj_final_namqd.dto.response.StudentResponse;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.UserRepository;
import com.tmdt.m3_pj_final_namqd.service.StudentService;
import com.tmdt.m3_pj_final_namqd.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "3. Student", description = "APIs for student")
public class StudentController {

    private final StudentService studentService;
    private final UserRepository userRepository;

    public StudentController(StudentService studentService, UserRepository userRepository) {
        this.studentService = studentService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MENTOR')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents(
            Authentication authentication) {

        String username = authentication.getName();
        User currentUser = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(
                ResponseUtil.success(
                        studentService.getAllStudents(currentUser),
                        "Lấy danh sách sinh viên thành công"
                )
        );
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(
            @PathVariable Long studentId,
            Authentication authentication
    ) {

        String username = authentication.getName();
        User currentUser = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(
                ResponseUtil.success(
                        studentService.getStudentById(studentId, currentUser),
                        "Lấy thông tin sinh viên thành công"
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody CreateStudentRequest request,
            Authentication authentication
    ) {

        String username = authentication.getName();

        User currentUser = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new AppException("User không tồn tại", HttpStatus.NOT_FOUND));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ResponseUtil.success(
                                studentService.createStudent(request, currentUser),
                                "Tạo sinh viên thành công"
                        )
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable("id") Long studentId,
            @Valid @RequestBody UpdateStudentRequest request,
            Authentication authentication
    ) {

        String username = authentication.getName();

        User currentUser = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new AppException("User không tồn tại", HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(
                ResponseUtil.success(
                        studentService.updateStudent(studentId, request, currentUser),
                        "Cập nhật sinh viên thành công"
                )
        );
    }
}
