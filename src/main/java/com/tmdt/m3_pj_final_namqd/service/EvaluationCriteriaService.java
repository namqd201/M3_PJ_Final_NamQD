package com.tmdt.m3_pj_final_namqd.service;

import com.tmdt.m3_pj_final_namqd.dto.request.EvaluationCriteriaRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.EvaluationCriteriaResponse;
import com.tmdt.m3_pj_final_namqd.entity.EvaluationCriteria;
import com.tmdt.m3_pj_final_namqd.entity.Role;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.EvaluationCriteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationCriteriaService {

    private final EvaluationCriteriaRepository repository;

    // get all
    public List<EvaluationCriteriaResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // detail
    public EvaluationCriteriaResponse getById(Long id) {
        EvaluationCriteria entity = repository.findById(id)
                .orElseThrow(() -> new AppException("Tiêu chí không tồn tại", HttpStatus.NOT_FOUND));

        return mapToResponse(entity);
    }

    // create
    @Transactional
    public EvaluationCriteriaResponse create(EvaluationCriteriaRequest request, User currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AppException("Chỉ admin được tạo", HttpStatus.FORBIDDEN);
        }

        if (repository.existsByCriterionName(request.getCriterionName())) {
            throw new AppException("Tên tiêu chí đã tồn tại", HttpStatus.CONFLICT);
        }

        validateScore(request.getMaxScore());

        EvaluationCriteria entity = new EvaluationCriteria();
        entity.setCriterionName(request.getCriterionName());
        entity.setMaxScore(request.getMaxScore());
        entity.setDescription(request.getDescription());

        return mapToResponse(repository.save(entity));
    }

    // update
    @Transactional
    public EvaluationCriteriaResponse update(Long id, EvaluationCriteriaRequest request, User currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AppException("Chỉ admin được cập nhật", HttpStatus.FORBIDDEN);
        }

        EvaluationCriteria entity = repository.findById(id)
                .orElseThrow(() -> new AppException("Tiêu chí không tồn tại", HttpStatus.NOT_FOUND));

        // check trùng tên (trừ chính nó)
        if (!entity.getCriterionName().equals(request.getCriterionName())
                && repository.existsByCriterionName(request.getCriterionName())) {
            throw new AppException("Tên tiêu chí đã tồn tại", HttpStatus.CONFLICT);
        }

        validateScore(request.getMaxScore());

        entity.setCriterionName(request.getCriterionName());
        entity.setMaxScore(request.getMaxScore());
        entity.setDescription(request.getDescription());

        return mapToResponse(repository.save(entity));
    }

    // delete
    @Transactional
    public void delete(Long id, User currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AppException("Chỉ admin được xóa", HttpStatus.FORBIDDEN);
        }

        EvaluationCriteria entity = repository.findById(id)
                .orElseThrow(() -> new AppException("Tiêu chí không tồn tại", HttpStatus.NOT_FOUND));

        //TODO: check đang dùng trong round / result

        repository.delete(entity);
    }

    // ================= VALIDATE =================
    private void validateScore(Double score) {
        if (score < 0) {
            throw new AppException("Max score phải >= 0", HttpStatus.BAD_REQUEST);
        }
    }

    private EvaluationCriteriaResponse mapToResponse(EvaluationCriteria entity) {
        return EvaluationCriteriaResponse.builder()
                .id(entity.getId())
                .criterionName(entity.getCriterionName())
                .maxScore(entity.getMaxScore())
                .description(entity.getDescription())
                .build();
    }
}
