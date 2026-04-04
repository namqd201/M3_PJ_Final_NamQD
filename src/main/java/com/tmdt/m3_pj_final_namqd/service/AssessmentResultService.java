package com.tmdt.m3_pj_final_namqd.service;

import com.tmdt.m3_pj_final_namqd.dto.request.assessment_result.AssessmentResultRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.assessment_result.AssessmentResultUpdateRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.AssessmentResultResponse;
import com.tmdt.m3_pj_final_namqd.entity.AssessmentResult;
import com.tmdt.m3_pj_final_namqd.entity.AssessmentRound;
import com.tmdt.m3_pj_final_namqd.entity.EvaluationCriteria;
import com.tmdt.m3_pj_final_namqd.entity.InternshipAssignment;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.AssessmentResultRepository;
import com.tmdt.m3_pj_final_namqd.repository.AssessmentRoundRepository;
import com.tmdt.m3_pj_final_namqd.repository.EvaluationCriteriaRepository;
import com.tmdt.m3_pj_final_namqd.repository.InternshipAssignmentRepository;
import com.tmdt.m3_pj_final_namqd.repository.RoundCriteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
@Service
@RequiredArgsConstructor
public class AssessmentResultService {

    private final AssessmentResultRepository resultRepo;
    private final InternshipAssignmentRepository assignmentRepo;
    private final AssessmentRoundRepository roundRepo;
    private final EvaluationCriteriaRepository criteriaRepo;
    private final RoundCriteriaRepository roundCriteriaRepo;

    @Transactional(readOnly = true)
    public List<AssessmentResultResponse> getAll(User currentUser, Long assignmentId, Long userId) {

        List<AssessmentResult> list = switch (currentUser.getRole()) {
            case ADMIN -> loadForAdmin(assignmentId, userId);
            case MENTOR -> loadForMentor(currentUser.getId(), assignmentId);
            case STUDENT -> loadForStudent(currentUser.getId(), assignmentId);
        };

        return list.stream()
                .sorted(Comparator.comparing(
                        AssessmentResult::getEvaluationDate,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .map(this::mapToResponse)
                .toList();
    }

    private List<AssessmentResult> loadForAdmin(Long assignmentId, Long userId) {
        if (assignmentId != null) {
            List<AssessmentResult> list = resultRepo.findByAssignment_Id(assignmentId);
            if (userId == null) {
                return list;
            }
            return list.stream().filter(r -> matchesUserFilter(r, userId)).toList();
        }
        if (userId != null) {
            return resultRepo.findAll().stream()
                    .filter(r -> matchesUserFilter(r, userId))
                    .toList();
        }
        return resultRepo.findAll();
    }

    private boolean matchesUserFilter(AssessmentResult r, Long userId) {
        var a = r.getAssignment();
        return Objects.equals(a.getStudent().getId(), userId)
                || Objects.equals(a.getMentor().getId(), userId)
                || Objects.equals(r.getEvaluator().getId(), userId);
    }

    private List<AssessmentResult> loadForMentor(Long mentorUserId, Long assignmentId) {
        if (assignmentId != null) {
            InternshipAssignment a = assignmentRepo.findById(assignmentId)
                    .orElseThrow(() -> new AppException("Không tìm thấy phân công", HttpStatus.NOT_FOUND));
            if (!a.getMentor().getId().equals(mentorUserId)) {
                throw new AppException("Không có quyền", HttpStatus.FORBIDDEN);
            }
            return resultRepo.findByAssignment_Id(assignmentId);
        }
        return resultRepo.findByAssignment_Mentor_Id(mentorUserId);
    }

    private List<AssessmentResult> loadForStudent(Long studentUserId, Long assignmentId) {
        if (assignmentId != null) {
            InternshipAssignment a = assignmentRepo.findById(assignmentId)
                    .orElseThrow(() -> new AppException("Không tìm thấy phân công", HttpStatus.NOT_FOUND));
            if (!a.getStudent().getId().equals(studentUserId)) {
                throw new AppException("Không có quyền", HttpStatus.FORBIDDEN);
            }
            return resultRepo.findByAssignment_Id(assignmentId);
        }
        return resultRepo.findByAssignment_Student_Id(studentUserId);
    }

    @Transactional
    public AssessmentResultResponse create(AssessmentResultRequest request, User currentUser) {

        InternshipAssignment assignment = assignmentRepo.findById(request.getAssignmentId())
                .orElseThrow(() -> new AppException("Phân công không tồn tại", HttpStatus.NOT_FOUND));

        if (!assignment.getMentor().getId().equals(currentUser.getId())) {
            throw new AppException("Bạn không phải mentor được phân công cho sinh viên này", HttpStatus.FORBIDDEN);
        }

        AssessmentRound round = roundRepo.findById(request.getRoundId())
                .orElseThrow(() -> new AppException("Đợt đánh giá không tồn tại", HttpStatus.NOT_FOUND));

        if (!round.getPhase().getId().equals(assignment.getPhase().getId())) {
            throw new AppException("Đợt đánh giá không thuộc đợt thực tập của phân công", HttpStatus.BAD_REQUEST);
        }

        EvaluationCriteria criterion = criteriaRepo.findById(request.getCriterionId())
                .orElseThrow(() -> new AppException("Tiêu chí không tồn tại", HttpStatus.NOT_FOUND));

        if (!roundCriteriaRepo.existsByAssessmentRound_IdAndEvaluationCriteria_Id(
                request.getRoundId(), request.getCriterionId())) {
            throw new AppException("Tiêu chí không thuộc đợt đánh giá này", HttpStatus.BAD_REQUEST);
        }

        if (resultRepo.existsByAssignment_IdAndAssessmentRound_IdAndCriterion_Id(
                request.getAssignmentId(), request.getRoundId(), request.getCriterionId())) {
            throw new AppException("Đã có kết quả cho tiêu chí này trong đợt đánh giá", HttpStatus.CONFLICT);
        }

        validateScore(request.getScore(), criterion);

        AssessmentResult entity = new AssessmentResult();
        entity.setAssignment(assignment);
        entity.setAssessmentRound(round);
        entity.setCriterion(criterion);
        entity.setScore(request.getScore());
        entity.setComments(request.getComments());
        entity.setEvaluator(currentUser);
        entity.setEvaluationDate(LocalDateTime.now());

        resultRepo.save(entity);

        return mapToResponse(entity);
    }

    @Transactional
    public AssessmentResultResponse update(Long resultId, AssessmentResultUpdateRequest request, User currentUser) {

        AssessmentResult entity = resultRepo.findById(resultId)
                .orElseThrow(() -> new AppException("Không tìm thấy kết quả đánh giá", HttpStatus.NOT_FOUND));

        if (!entity.getEvaluator().getId().equals(currentUser.getId())) {
            throw new AppException("Chỉ được sửa kết quả do chính bạn tạo", HttpStatus.FORBIDDEN);
        }

        validateScore(request.getScore(), entity.getCriterion());

        entity.setScore(request.getScore());
        entity.setComments(request.getComments());
        entity.setEvaluationDate(LocalDateTime.now());

        resultRepo.save(entity);

        return mapToResponse(entity);
    }

    private void validateScore(Double score, EvaluationCriteria criterion) {
        if (score == null || score < 0) {
            throw new AppException("Điểm phải >= 0", HttpStatus.BAD_REQUEST);
        }
        if (criterion.getMaxScore() != null && score > criterion.getMaxScore()) {
            throw new AppException("Điểm không được vượt quá điểm tối đa của tiêu chí", HttpStatus.BAD_REQUEST);
        }
    }

    private AssessmentResultResponse mapToResponse(AssessmentResult e) {
        var a = e.getAssignment();
        return AssessmentResultResponse.builder()
                .id(e.getId())
                .assignmentId(a.getId())
                .studentId(a.getStudent().getId())
                .studentName(a.getStudent().getUser().getFullName())
                .mentorId(a.getMentor().getId())
                .mentorName(a.getMentor().getUser().getFullName())
                .phaseId(a.getPhase().getId())
                .phaseName(a.getPhase().getPhaseName())
                .roundId(e.getAssessmentRound().getId())
                .roundName(e.getAssessmentRound().getRoundName())
                .criterionId(e.getCriterion().getId())
                .criterionName(e.getCriterion().getCriterionName())
                .score(e.getScore())
                .comments(e.getComments())
                .evaluatorId(e.getEvaluator().getId())
                .evaluatorName(e.getEvaluator().getFullName())
                .evaluationDate(e.getEvaluationDate())
                .build();
    }
}
