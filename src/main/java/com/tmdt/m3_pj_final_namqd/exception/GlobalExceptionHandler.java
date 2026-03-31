package com.tmdt.m3_pj_final_namqd.exception;

import com.tmdt.m3_pj_final_namqd.config.message.Messages;
import com.tmdt.m3_pj_final_namqd.dto.response.ApiResponse;
import com.tmdt.m3_pj_final_namqd.dto.response.ErrorResponse;
import com.tmdt.m3_pj_final_namqd.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Custom exception
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtil.error("ERR500", ex.getMessage()));
    }

    // Unauthorized
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied() {

        ErrorResponse error = ErrorResponse.builder()
                .code("Unauthorized")
                .message(Messages.get("Unauthorized"))
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

}
