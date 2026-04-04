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

import java.util.List;

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

        List<Student> students;

        if (currentUser.getRole() == Role.ADMIN) {
            students = studentRepository.findAll();
        } else {
            // MENTOR — controller chỉ cho ADMIN + MENTOR
            List<InternshipAssignment> assignments =
                    internshipAssignmentRepository.findByMentor_User_IdAndStatus(
                            currentUser.getId(),
                            AssignmentStatus.APPROVED
                    );

            students = assignments.stream()
                    .map(InternshipAssignment::getStudent)
                    .distinct()
                    .toList();
        }

        return students.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get detail student
    public StudentResponse getStudentById(Long studentId, User currentUser) {

        // find student (đã auto filter soft delete)
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException("Không tìm thấy sinh viên", HttpStatus.NOT_FOUND));

        // authorization
        Role role = currentUser.getRole();

        if (role == Role.ADMIN) {
            // OK
        }
        else if (role == Role.MENTOR) {

            boolean isAssigned = internshipAssignmentRepository
                    .existsByMentor_User_IdAndStudent_IdAndStatus(
                            currentUser.getId(),
                            studentId,
                            AssignmentStatus.APPROVED
                    );

            if (!isAssigned) {
                throw new AppException("Bạn không có quyền xem sinh viên này", HttpStatus.FORBIDDEN);
            }
        }
        else if (role == Role.STUDENT) {

            if (!student.getId().equals(currentUser.getId())) {
                throw new AppException("Chỉ được xem thông tin của chính bạn", HttpStatus.FORBIDDEN);
            }
        } else {
            throw new IllegalStateException("Unexpected role: " + role);
        }
        return mapToResponse(student);
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
}
