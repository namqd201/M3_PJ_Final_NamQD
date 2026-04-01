package com.tmdt.m3_pj_final_namqd.exception;

import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ===== BUSINESS EXCEPTION =====
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException ex) {

        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.builder()
                        .success(false)
                        .message(ex.getCode())
                        .data(null)
                        .errors(null)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // ===== VALIDATION =====
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> Map.of(
                        "field", err.getField(),
                        "message", err.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("Dữ liệu không hợp lệ")
                        .data(null)
                        .errors(errors)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // ===== UNAUTHORIZED (JWT sai / thiếu token) =====
    @ExceptionHandler(org.springframework.security.authentication.AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorized() {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("Unauthorized")
                        .data(null)
                        .errors(null)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // ===== FORBIDDEN =====
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied() {

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("Forbidden")
                        .data(null)
                        .errors(null)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // ===== SYSTEM ERROR =====
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("Internal Server Error")
                        .data(null)
                        .errors(null)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

}
