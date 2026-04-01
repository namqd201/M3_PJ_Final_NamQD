package com.tmdt.m3_pj_final_namqd.controller;

import com.tmdt.m3_pj_final_namqd.dto.request.mentor.CreateMentorRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.mentor.UpdateMentorRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import com.tmdt.m3_pj_final_namqd.dto.response.MentorResponse;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.UserRepository;
import com.tmdt.m3_pj_final_namqd.service.AuthService;
import com.tmdt.m3_pj_final_namqd.service.MentorService;
import com.tmdt.m3_pj_final_namqd.service.UserService;
import com.tmdt.m3_pj_final_namqd.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentors")
@Tag(name = "4. Mentor", description = "APIs for mentor")
public class MentorController {

    private final UserRepository userRepository;
    private  final MentorService mentorService;

    public MentorController(UserRepository userRepository, MentorService mentorService) {
        this.userRepository = userRepository;
        this.mentorService = mentorService;
    }

    private User getCurrentUser(Authentication auth) {
        return userRepository.findByUsernameAndIsDeletedFalse(auth.getName())
                .orElseThrow(() -> new AppException("User không tồn tại", HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MentorResponse>>> getAll(Authentication auth) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        mentorService.getAll(),
                        "Lấy danh sách mentor thành công"
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MentorResponse>> getById(
            @PathVariable Long id,
            Authentication auth
    ) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        mentorService.getById(id, getCurrentUser(auth)),
                        "Lấy chi tiết mentor thành công"
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MentorResponse>> create(
            @Valid @RequestBody CreateMentorRequest request,
            Authentication auth
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ResponseUtil.success(
                                mentorService.create(request, getCurrentUser(auth)),
                                "Tạo mentor thành công"
                        )
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MentorResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMentorRequest request,
            Authentication auth
    ) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        mentorService.update(id, request, getCurrentUser(auth)),
                        "Cập nhật mentor thành công"
                )
        );
    }
}
