package com.tmdt.m3_pj_final_namqd.service;

import com.tmdt.m3_pj_final_namqd.dto.request.student.CreateStudentRequest;
import com.tmdt.m3_pj_final_namqd.dto.request.student.UpdateStudentRequest;
import com.tmdt.m3_pj_final_namqd.dto.response.StudentResponse;
import com.tmdt.m3_pj_final_namqd.entity.*;
import com.tmdt.m3_pj_final_namqd.exception.AppException;
import com.tmdt.m3_pj_final_namqd.repository.InternshipAssignmentRepository;
import com.tmdt.m3_pj_final_namqd.repository.StudentRepository;
import com.tmdt.m3_pj_final_namqd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StudentService {
    private final InternshipAssignmentRepository  internshipAssignmentRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public StudentService(InternshipAssignmentRepository internshipAssignmentRepository, StudentRepository studentRepository, UserRepository userRepository) {
        this.internshipAssignmentRepository = internshipAssignmentRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    //Get all student
    public List<StudentResponse> getAllStudents(User currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            List<StudentResponse> studentProfiles = studentRepository.findAll()
                    .stream()
                    .map(this::mapToResponse)
                    .toList();

            Set<Long> existingIds = new HashSet<>(
                    studentProfiles.stream().map(StudentResponse::getId).toList()
            );

            List<StudentResponse> missingProfiles = userRepository.findByRoleAndIsDeletedFalse(Role.STUDENT)
                    .stream()
                    .filter(user -> !existingIds.contains(user.getId()))
                    .map(user -> mapToResponse(user, null))
                    .toList();

            return java.util.stream.Stream.concat(
                    studentProfiles.stream(),
                    missingProfiles.stream()
            ).toList();
        }

        // MENTOR — controller chỉ cho ADMIN + MENTOR
        List<Student> students = internshipAssignmentRepository
                .findAssignedStudentsByMentorUserId(currentUser.getId());

        return students.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get detail student
    public StudentResponse getStudentById(Long studentId, User currentUser) {

        // authorization
        Role role = currentUser.getRole();

        if (role == Role.ADMIN) {
            Student student = studentRepository.findById(studentId)
                    .or(() -> studentRepository.findByUser_Id(studentId))
                    .orElse(null);
            if (student != null) {
                return mapToResponse(student);
            }

            User studentUser = userRepository.findByIdAndIsDeletedFalse(studentId)
                    .orElseThrow(() -> new AppException("Không tìm thấy sinh viên", HttpStatus.NOT_FOUND));
            if (studentUser.getRole() != Role.STUDENT) {
                throw new AppException("Không tìm thấy sinh viên", HttpStatus.NOT_FOUND);
            }
            return mapToResponse(studentUser, null);
        }
        else if (role == Role.MENTOR) {
            Student student = studentRepository.findById(studentId)
                    .or(() -> studentRepository.findByUser_Id(studentId))
                    .orElseThrow(() -> new AppException("Không tìm thấy sinh viên", HttpStatus.NOT_FOUND));

            boolean isAssigned = internshipAssignmentRepository
                    .existsAssignedStudentForMentor(
                            currentUser.getId(),
                            studentId
                    );

            if (!isAssigned) {
                throw new AppException("Bạn không có quyền xem sinh viên này", HttpStatus.FORBIDDEN);
            }
            return mapToResponse(student);
        }
        else if (role == Role.STUDENT) {
            Student student = studentRepository.findById(studentId).orElse(null);

            if (!studentId.equals(currentUser.getId())) {
                throw new AppException("Chỉ được xem thông tin của chính bạn", HttpStatus.FORBIDDEN);
            }
            if (student != null) {
                return mapToResponse(student);
            }
            return mapToResponse(currentUser, null);
        } else {
            throw new IllegalStateException("Unexpected role: " + role);
        }
    }

    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {

        // FIND USER
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException("User không tồn tại", HttpStatus.NOT_FOUND));

        // CHECK ROLE = STUDENT
        if (user.getRole() != Role.STUDENT) {
            throw new AppException("User không phải role STUDENT", HttpStatus.BAD_REQUEST);
        }

        // CHECK USER ĐÃ LÀ STUDENT CHƯA
        if (studentRepository.existsById(user.getId())) {
            throw new AppException("User đã là sinh viên", HttpStatus.CONFLICT);
        }

        // CHECK STUDENT CODE
        if (studentRepository.existsByStudentCode(request.getStudentCode())) {
            throw new AppException("Mã sinh viên đã tồn tại", HttpStatus.CONFLICT);
        }

        // CREATE STUDENT (MapsId)
        Student student = new Student();
        student.setUser(user);
        student.setStudentCode(request.getStudentCode());
        student.setMajor(request.getMajor());
        student.setClassName(request.getClassName());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAddress(request.getAddress());

        student = studentRepository.save(student);

        return mapToResponse(student);
    }

    @Transactional
    public StudentResponse updateStudent(
            Long studentId,
            UpdateStudentRequest request,
            User currentUser
    ) {

        // FIND STUDENT (đã auto filter is_deleted=false)
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException("Sinh viên không tồn tại", HttpStatus.NOT_FOUND));

        if (currentUser.getRole() == Role.STUDENT
                && !student.getId().equals(currentUser.getId())) {
            throw new AppException("Bạn chỉ được cập nhật thông tin của mình", HttpStatus.FORBIDDEN);
        }

        // UPDATE (partial update)
        if (request.getMajor() != null) {
            student.setMajor(request.getMajor());
        }

        if (request.getClassName() != null) {
            student.setClassName(request.getClassName());
        }

        if (request.getDateOfBirth() != null) {
            student.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getAddress() != null) {
            student.setAddress(request.getAddress());
        }

        student = studentRepository.save(student);

        return mapToResponse(student);
    }

    private StudentResponse mapToResponse(Student student) {

        User user = student.getUser();

        return StudentResponse.builder()
                .id(student.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .studentCode(student.getStudentCode())
                .major(student.getMajor())
                .className(student.getClassName())
                .dateOfBirth(student.getDateOfBirth())
                .address(student.getAddress())
                .build();
    }

    private StudentResponse mapToResponse(User user, Student studentProfile) {
        return StudentResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .studentCode(studentProfile != null ? studentProfile.getStudentCode() : null)
                .major(studentProfile != null ? studentProfile.getMajor() : null)
                .className(studentProfile != null ? studentProfile.getClassName() : null)
                .dateOfBirth(studentProfile != null ? studentProfile.getDateOfBirth() : null)
                .address(studentProfile != null ? studentProfile.getAddress() : null)
                .build();
    }
}
