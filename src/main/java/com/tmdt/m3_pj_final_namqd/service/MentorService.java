package com.tmdt.m3_pj_final_namqd.service;

import com.tmdt.m3_pj_final_namqd.dto.request.mentor.CreateMentorRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.mentor.UpdateMentorRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.MentorResponse;
import com.tmdt.m3_pj_final_namqd.entity.Mentor;
import com.tmdt.m3_pj_final_namqd.entity.Role;
import com.tmdt.m3_pj_final_namqd.entity.User;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.MentorRepository;
import com.tmdt.m3_pj_final_namqd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorService {
    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;

    // get all
    public List<MentorResponse> getAll() {
        return mentorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // get by id
    public MentorResponse getById(Long id, User currentUser) {

        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new AppException("Mentor không tồn tại", HttpStatus.NOT_FOUND));

        if (currentUser.getRole() == Role.MENTOR) {
            if (!mentor.getId().equals(currentUser.getId())) {
                throw new AppException("Chỉ được xem thông tin của mình", HttpStatus.FORBIDDEN);
            }
        }

        return mapToResponse(mentor);
    }

    // create
    @Transactional
    public MentorResponse create(CreateMentorRequest request, User currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AppException("Chỉ admin được tạo mentor", HttpStatus.FORBIDDEN);
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException("User không tồn tại", HttpStatus.NOT_FOUND));

        if (user.getRole() != Role.MENTOR) {
            throw new AppException("User không phải MENTOR", HttpStatus.BAD_REQUEST);
        }

        if (mentorRepository.existsById(user.getId())) {
            throw new AppException("User đã là mentor", HttpStatus.CONFLICT);
        }

        Mentor mentor = new Mentor();
        mentor.setUser(user);
        mentor.setDepartment(request.getDepartment());
        mentor.setAcademicRank(request.getAcademicRank());

        mentor = mentorRepository.save(mentor);

        return mapToResponse(mentor);
    }

    // update
    @Transactional
    public MentorResponse update(Long id, UpdateMentorRequest request, User currentUser) {

        Mentor mentor = mentorRepository.findById(id)
                .orElseThrow(() -> new AppException("Mentor không tồn tại", HttpStatus.NOT_FOUND));

        if (currentUser.getRole() == Role.ADMIN) {
            // OK
        } else if (currentUser.getRole() == Role.MENTOR) {

            if (!mentor.getId().equals(currentUser.getId())) {
                throw new AppException("Chỉ được sửa thông tin của mình", HttpStatus.FORBIDDEN);
            }

        } else {
            throw new AppException("Không có quyền", HttpStatus.FORBIDDEN);
        }

        if (request.getDepartment() != null) {
            mentor.setDepartment(request.getDepartment());
        }

        if (request.getAcademicRank() != null) {
            mentor.setAcademicRank(request.getAcademicRank());
        }

        mentor = mentorRepository.save(mentor);

        return mapToResponse(mentor);
    }

    private MentorResponse mapToResponse(Mentor mentor) {

        User user = mentor.getUser();

        return MentorResponse.builder()
                .id(mentor.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .department(mentor.getDepartment())
                .academicRank(mentor.getAcademicRank())
                .build();
    }
}
