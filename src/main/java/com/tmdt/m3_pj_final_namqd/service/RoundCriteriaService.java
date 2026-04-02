package com.tmdt.m3_pj_final_namqd.service;

import com.tmdt.m3_pj_final_namqd.dto.request.round_criteria.RoundCriteriaRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.RoundCriteriaResponse;
import com.tmdt.m3_pj_final_namqd.entity.*;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.AssessmentResultRepository;
import com.tmdt.m3_pj_final_namqd.repository.AssessmentRoundRepository;
import com.tmdt.m3_pj_final_namqd.repository.EvaluationCriteriaRepository;
import com.tmdt.m3_pj_final_namqd.repository.RoundCriteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoundCriteriaService {
    private final RoundCriteriaRepository repository;
    private final AssessmentRoundRepository roundRepo;
    private final EvaluationCriteriaRepository criteriaRepo;
    private final AssessmentResultRepository resultRepo;

    public List<RoundCriteriaResponse> getByRound(Long roundId) {

        List<RoundCriteria> list = repository.findByAssessmentRound_Id(roundId);

        return list.stream().map(this::mapToResponse).toList();
    }

    public RoundCriteriaResponse getById(Long id) {
        RoundCriteria entity = repository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy", HttpStatus.NOT_FOUND));

        return mapToResponse(entity);
    }

    public RoundCriteriaResponse create(RoundCriteriaRequest request) {

        AssessmentRound round = roundRepo.findById(request.getRoundId())
                .orElseThrow(() -> new AppException("Round không tồn tại", HttpStatus.NOT_FOUND));

        EvaluationCriteria criteria = criteriaRepo.findById(request.getCriteriaId())
                .orElseThrow(() -> new AppException("Criteria không tồn tại", HttpStatus.NOT_FOUND));

        // Không cho trùng
        boolean exists = repository.existsByAssessmentRound_IdAndEvaluationCriteria_Id(
                request.getRoundId(), request.getCriteriaId()
        );

        if (exists) {
            throw new AppException("Tiêu chí đã tồn tại trong round", HttpStatus.CONFLICT);
        }

        RoundCriteria entity = new RoundCriteria();
        entity.setAssessmentRound(round);
        entity.setEvaluationCriteria(criteria);
        entity.setWeight(request.getWeight());

        repository.save(entity);

        return mapToResponse(entity);
    }

    public RoundCriteriaResponse update(Long id, RoundCriteriaRequest request) {


        RoundCriteria entity = repository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy", HttpStatus.NOT_FOUND));

        // Nếu đã có result → không cho sửa
        boolean hasResult = resultRepo.existsByAssessmentRound_IdAndCriterion_Id(
                entity.getAssessmentRound().getId(),
                entity.getEvaluationCriteria().getId()
        );

        if (hasResult) {
            throw new AppException("Đã có dữ liệu chấm điểm, không thể sửa", HttpStatus.CONFLICT);
        }

        entity.setWeight(request.getWeight());

        repository.save(entity);

        return mapToResponse(entity);
    }

    public void delete(Long id) {


        RoundCriteria entity = repository.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy", HttpStatus.NOT_FOUND));

        // Nếu đã có result → không cho xóa
        boolean hasResult = resultRepo.existsByAssessmentRound_IdAndCriterion_Id(
                entity.getAssessmentRound().getId(),
                entity.getEvaluationCriteria().getId()
        );

        if (hasResult) {
            throw new AppException("Đã có dữ liệu chấm điểm, không thể xóa", HttpStatus.CONFLICT);
        }

        repository.delete(entity);
    }


    private RoundCriteriaResponse mapToResponse(RoundCriteria e) {
        return RoundCriteriaResponse.builder()
                .id(e.getId())
                .roundId(e.getAssessmentRound().getId())
                .roundName(e.getAssessmentRound().getRoundName())
                .criteriaId(e.getEvaluationCriteria().getId())
                .criteriaName(e.getEvaluationCriteria().getCriterionName())
                .weight(e.getWeight())
                .build();
    }
}
