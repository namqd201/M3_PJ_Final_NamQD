package com.tmdt.m3_pj_final_namqd.service;

import com.tmdt.m3_pj_final_namqd.dto.request.assessment_round.AssessmentRoundRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.assessment_round.CriterionWeightRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.AssessmentRoundResponse;
import com.tmdt.m3_pj_final_namqd.dto.response.CriterionWeightResponse;
import com.tmdt.m3_pj_final_namqd.entity.*;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentRoundService {

    private final AssessmentRoundRepository roundRepo;
    private final RoundCriteriaRepository roundCriteriaRepo;
    private final EvaluationCriteriaRepository criteriaRepo;
    private final InternshipPhaseRepository phaseRepo;
    private final AssessmentResultRepository resultRepo;

    // get all
    public List<AssessmentRoundResponse> getAll(Long phaseId) {
        List<AssessmentRound> rounds = (phaseId != null)
                ? roundRepo.findByPhase_Id(phaseId)
                : roundRepo.findAll();

        return rounds.stream().map(this::mapToResponse).toList();
    }

    // detail
    public AssessmentRoundResponse getById(Long id) {
        AssessmentRound round = roundRepo.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy đợt đánh giá", HttpStatus.NOT_FOUND));

        return mapToResponse(round);
    }

    // create
    @Transactional
    public AssessmentRoundResponse create(AssessmentRoundRequest request) {

        InternshipPhase phase = phaseRepo.findById(request.getPhaseId())
                .orElseThrow(() -> new RuntimeException("Phase not found"));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new AppException("Ngày bắt đầu phải trước ngày kết thúc", HttpStatus.BAD_REQUEST);
        }
        AssessmentRound round = new AssessmentRound();
        round.setPhase(phase);
        round.setRoundName(request.getRoundName());
        round.setStartDate(request.getStartDate());
        round.setEndDate(request.getEndDate());
        round.setDescription(request.getDescription());
        round.setActive(true);

        round = roundRepo.save(round);

        saveCriteria(round, request.getCriteria());

        return mapToResponse(round);
    }

    // update
    @Transactional
    public AssessmentRoundResponse update(Long id, AssessmentRoundRequest request) {

        AssessmentRound round = roundRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Round not found"));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new AppException("Ngày bắt đầu phải trước ngày kết thúc", HttpStatus.BAD_REQUEST);
        }

        round.setRoundName(request.getRoundName());
        round.setStartDate(request.getStartDate());
        round.setEndDate(request.getEndDate());
        round.setDescription(request.getDescription());
        round.setActive(request.getIsActive());

        roundRepo.save(round);

        // xoá cũ -> insert lại
        roundCriteriaRepo.deleteByAssessmentRound_Id(id);
        saveCriteria(round, request.getCriteria());

        return mapToResponse(round);
    }

    // delete
    public void delete(Long id) {
        AssessmentRound round = roundRepo.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy đợt đánh giá", HttpStatus.NOT_FOUND));

        //  check data liên quan
        boolean hasResult = resultRepo.existsByAssessmentRound_Id(id);

        if (hasResult) {
            throw new AppException(
                    "Đợt đánh giá đã có dữ liệu chấm điểm, không thể xóa",
                    HttpStatus.CONFLICT
            );
        }

        // Soft delete
        roundRepo.delete(round);
    }

    private void saveCriteria(AssessmentRound round, List<CriterionWeightRequest> criteria) {

        if (criteria == null || criteria.isEmpty()) return;

        for (CriterionWeightRequest c : criteria) {

            EvaluationCriteria ec = criteriaRepo.findById(c.getCriterionId())
                    .orElseThrow(() -> new RuntimeException("Criterion not found"));

            RoundCriteria rc = new RoundCriteria();
            rc.setAssessmentRound(round);
            rc.setEvaluationCriteria(ec);
            rc.setWeight(c.getWeight());

            roundCriteriaRepo.save(rc);
        }
    }


    private AssessmentRoundResponse mapToResponse(AssessmentRound round) {

        List<RoundCriteria> rcList = roundCriteriaRepo.findByAssessmentRound_Id(round.getId());

        List<CriterionWeightResponse> criteria = rcList.stream()
                .map(rc -> CriterionWeightResponse.builder()
                        .criterionId(rc.getEvaluationCriteria().getId())
                        .criterionName(rc.getEvaluationCriteria().getCriterionName())
                        .weight(rc.getWeight())
                        .build()
                ).toList();

        return AssessmentRoundResponse.builder()
                .id(round.getId())
                .roundName(round.getRoundName())
                .startDate(round.getStartDate())
                .endDate(round.getEndDate())
                .description(round.getDescription())
                .isActive(round.isActive())
                .phaseId(round.getPhase().getId())
                .criteria(criteria)
                .build();
    }
}
