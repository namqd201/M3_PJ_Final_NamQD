package com.tmdt.m3_pj_final_namqd.service;

import com.tmdt.m3_pj_final_namqd.dto.request.internship_phase.InternshipPhaseRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.InternshipPhaseResponse;
import com.tmdt.m3_pj_final_namqd.entity.InternshipPhase;
import com.tmdt.m3_pj_final_namqd.entity.Role;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.InternshipPhaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipPhaseService {

    private final InternshipPhaseRepository phaseRepository;

    // get all
    public List<InternshipPhaseResponse> getAll() {
        return phaseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // get detail
    public InternshipPhaseResponse getById(Long id) {
        InternshipPhase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new AppException("Giai đoạn không tồn tại", HttpStatus.NOT_FOUND));

        return mapToResponse(phase);
    }

    // create
    @Transactional
    public InternshipPhaseResponse create(InternshipPhaseRequest request, User currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AppException("Chỉ admin được tạo", HttpStatus.FORBIDDEN);
        }

        if (phaseRepository.existsByPhaseName(request.getPhaseName())) {
            throw new AppException("Tên giai đoạn đã tồn tại", HttpStatus.CONFLICT);
        }

        validateDate(request.getStartDate(), request.getEndDate());

        InternshipPhase phase = new InternshipPhase();
        phase.setPhaseName(request.getPhaseName());
        phase.setStartDate(request.getStartDate());
        phase.setEndDate(request.getEndDate());
        phase.setDescription(request.getDescription());

        return mapToResponse(phaseRepository.save(phase));
    }

    // update
    @Transactional
    public InternshipPhaseResponse update(Long id, InternshipPhaseRequest request, User currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AppException("Chỉ admin được cập nhật", HttpStatus.FORBIDDEN);
        }

        InternshipPhase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new AppException("Giai đoạn không tồn tại", HttpStatus.NOT_FOUND));

        validateDate(request.getStartDate(), request.getEndDate());

        phase.setPhaseName(request.getPhaseName());
        phase.setStartDate(request.getStartDate());
        phase.setEndDate(request.getEndDate());
        phase.setDescription(request.getDescription());

        return mapToResponse(phaseRepository.save(phase));
    }

    // delete
    @Transactional
    public void delete(Long id, User currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AppException("Chỉ admin được xóa", HttpStatus.FORBIDDEN);
        }

        InternshipPhase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new AppException("Giai đoạn không tồn tại", HttpStatus.NOT_FOUND));

        // 🔥 TODO nâng cao: check assignment đang dùng phase này

        phaseRepository.delete(phase);
    }

    // validate date
    private void validateDate(java.time.LocalDate start, java.time.LocalDate end) {
        if (start.isAfter(end)) {
            throw new AppException("Ngày bắt đầu phải trước ngày kết thúc", HttpStatus.BAD_REQUEST);
        }
    }

    private InternshipPhaseResponse mapToResponse(InternshipPhase phase) {
        return InternshipPhaseResponse.builder()
                .id(phase.getId())
                .phaseName(phase.getPhaseName())
                .startDate(phase.getStartDate())
                .endDate(phase.getEndDate())
                .description(phase.getDescription())
                .build();
    }
}
