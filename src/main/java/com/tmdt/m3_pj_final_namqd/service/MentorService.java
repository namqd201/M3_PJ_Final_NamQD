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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MentorService {
    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;

    // get all
    public List<MentorResponse> getAll() {
        List<MentorResponse> mentorProfiles = mentorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        Set<Long> existingIds = new HashSet<>(
                mentorProfiles.stream().map(MentorResponse::getId).toList()
        );

        List<MentorResponse> missingProfiles = userRepository.findByRoleAndIsDeletedFalse(Role.MENTOR)
                .stream()
                .filter(user -> !existingIds.contains(user.getId()))
                .map(user -> mapToResponse(user, null))
                .toList();

        return java.util.stream.Stream.concat(
                mentorProfiles.stream(),
                missingProfiles.stream()
        ).toList();
    }

    // get by id
    public MentorResponse getById(Long id, User currentUser) {
        if (currentUser.getRole() == Role.MENTOR && !id.equals(currentUser.getId())) {
            throw new AppException("Chỉ được xem thông tin của mình", HttpStatus.FORBIDDEN);
        }

        Mentor mentor = mentorRepository.findById(id)
                .or(() -> mentorRepository.findByUser_Id(id))
                .orElse(null);
        if (mentor != null) {
            return mapToResponse(mentor);
        }

        User mentorUser = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException("Mentor không tồn tại", HttpStatus.NOT_FOUND));
        if (mentorUser.getRole() != Role.MENTOR) {
            throw new AppException("Mentor không tồn tại", HttpStatus.NOT_FOUND);
        }

        return mapToResponse(mentorUser, null);
    }

    // create
    @Transactional
    public MentorResponse create(CreateMentorRequest request) {

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

    private MentorResponse mapToResponse(User user, Mentor mentorProfile) {
        return MentorResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .department(mentorProfile != null ? mentorProfile.getDepartment() : null)
                .academicRank(mentorProfile != null ? mentorProfile.getAcademicRank() : null)
                .build();
    }
}
