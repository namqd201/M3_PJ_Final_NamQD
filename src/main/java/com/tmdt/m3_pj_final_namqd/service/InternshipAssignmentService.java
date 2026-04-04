package com.tmdt.m3_pj_final_namqd.service;

import com.tmdt.m3_pj_final_namqd.dto.request.internship_assignment.InternshipAssignmentRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.InternshipAssignmentResponse;
import com.tmdt.m3_pj_final_namqd.entity.AssignmentStatus;
import com.tmdt.m3_pj_final_namqd.entity.InternshipAssignment;
import com.tmdt.m3_pj_final_namqd.entity.InternshipPhase;
import com.tmdt.m3_pj_final_namqd.entity.Mentor;
import com.tmdt.m3_pj_final_namqd.entity.Role;
import com.tmdt.m3_pj_final_namqd.entity.Student;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.InternshipAssignmentRepository;
import com.tmdt.m3_pj_final_namqd.repository.InternshipPhaseRepository;
import com.tmdt.m3_pj_final_namqd.repository.MentorRepository;
import com.tmdt.m3_pj_final_namqd.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternshipAssignmentService {
    private final InternshipAssignmentRepository repo;
    private final StudentRepository studentRepo;
    private final MentorRepository mentorRepo;
    private final InternshipPhaseRepository phaseRepo;

    // ================= GET LIST =================
    public List<InternshipAssignmentResponse> getAll(User currentUser) {

        List<InternshipAssignment> list = switch (currentUser.getRole()) {
            case ADMIN -> repo.findAll();
            case MENTOR -> repo.findByMentor_Id(currentUser.getId());
            case STUDENT -> repo.findByStudent_Id(currentUser.getId());
        };

        return list.stream().map(this::mapToResponse).toList();
    }

    // ================= GET DETAIL =================
    public InternshipAssignmentResponse getById(Long id, User currentUser) {

        InternshipAssignment entity = repo.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy", HttpStatus.NOT_FOUND));

        // check quyền
        if (currentUser.getRole() == Role.MENTOR &&
                !entity.getMentor().getId().equals(currentUser.getId())) {
            throw new AppException("Không có quyền", HttpStatus.FORBIDDEN);
        }

        if (currentUser.getRole() == Role.STUDENT &&
                !entity.getStudent().getId().equals(currentUser.getId())) {
            throw new AppException("Không có quyền", HttpStatus.FORBIDDEN);
        }

        return mapToResponse(entity);
    }

    // create
    public InternshipAssignmentResponse create(InternshipAssignmentRequest request) {

        Student student = studentRepo.findById(request.getStudentId())
                .orElseThrow(() -> new AppException("Student không tồn tại", HttpStatus.NOT_FOUND));

        Mentor mentor = mentorRepo.findById(request.getMentorId())
                .orElseThrow(() -> new AppException("Mentor không tồn tại", HttpStatus.NOT_FOUND));

        InternshipPhase phase = phaseRepo.findById(request.getPhaseId())
                .orElseThrow(() -> new AppException("Phase không tồn tại", HttpStatus.NOT_FOUND));

        // không cho trùng
        if (repo.existsByStudent_IdAndPhase_Id(request.getStudentId(), request.getPhaseId())) {
            throw new AppException("Sinh viên đã được phân công trong phase này", HttpStatus.CONFLICT);
        }

        InternshipAssignment entity = new InternshipAssignment();
        entity.setStudent(student);
        entity.setMentor(mentor);
        entity.setPhase(phase);
        entity.setStatus(AssignmentStatus.PENDING);

        repo.save(entity);

        log.info("Internship assignment created: id={}, studentId={}, mentorId={}, phaseId={}, status={}",
                entity.getId(), student.getId(), mentor.getId(), phase.getId(), entity.getStatus());

        return mapToResponse(entity);
    }

    // updaate status
    public InternshipAssignmentResponse updateStatus(Long id, String status) {

        InternshipAssignment entity = repo.findById(id)
                .orElseThrow(() -> new AppException("Không tìm thấy", HttpStatus.NOT_FOUND));

        AssignmentStatus newStatus;
        try {
            newStatus = AssignmentStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new AppException("Trạng thái không hợp lệ", HttpStatus.BAD_REQUEST);
        }
        AssignmentStatus previous = entity.getStatus();
        entity.setStatus(newStatus);

        repo.save(entity);

        log.info("Assignment status changed: id={}, {} -> {}", id, previous, newStatus);

        return mapToResponse(entity);
    }

    private InternshipAssignmentResponse mapToResponse(InternshipAssignment e) {

        return InternshipAssignmentResponse.builder()
                .id(e.getId())
                .studentId(e.getStudent().getId())
                .studentName(e.getStudent().getUser().getFullName())
                .mentorId(e.getMentor().getId())
                .mentorName(e.getMentor().getUser().getFullName())
                .phaseId(e.getPhase().getId())
                .phaseName(e.getPhase().getPhaseName())
                .status(e.getStatus() != null ? e.getStatus().name() : null)
                .build();
    }
}
