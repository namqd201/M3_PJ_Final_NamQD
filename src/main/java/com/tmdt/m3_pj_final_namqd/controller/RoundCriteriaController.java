package com.tmdt.m3_pj_final_namqd.controller;

import com.tmdt.m3_pj_final_namqd.dto.request.round_criteria.RoundCriteriaRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import com.tmdt.m3_pj_final_namqd.dto.response.AssessmentRoundResponse;
import com.tmdt.m3_pj_final_namqd.entity.RoundCriteria;
import com.tmdt.m3_pj_final_namqd.service.RoundCriteriaService;
import com.tmdt.m3_pj_final_namqd.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/round_criteria")
@RequiredArgsConstructor
public class RoundCriteriaController {

    private final RoundCriteriaService service;

    // get list by round
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getByRound(@RequestParam Long roundId) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.getByRound(roundId),
                        "Lấy danh sách tiêu chí trong đợt đánh giá thành công"
                )
        );
    }

    // detail
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getById(@PathVariable Long id) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.getById(id),
                        "Lấy chi tiết tiêu chí trong đợt đánh giá thành công"
                )
        );
    }

    // create
    @PostMapping
    public ResponseEntity<ApiResponse<?>> create(@RequestBody RoundCriteriaRequest request) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.create(request),
                        "Thêm tiêu chí vào đợt đánh giá thành công"
                )
        );
    }

    // update
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id,
                                                 @RequestBody RoundCriteriaRequest request) {

        return ResponseEntity.ok(
                ResponseUtil.success(
                        service.update(id, request),
                        "Cập nhật tiêu chí trong đợt đánh giá thành công"
                )
        );
    }

    // delete
    @DeleteMapping("/{id}")
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