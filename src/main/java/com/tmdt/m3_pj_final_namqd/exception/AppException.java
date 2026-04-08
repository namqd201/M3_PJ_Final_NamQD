package com.tmdt.m3_pj_final_namqd.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import java.util.Map;

@Getter
public class AppException extends RuntimeException {

    private final String code;
    private final HttpStatus status;
    private final Object errors;

    public AppException(String code, HttpStatus status) {
        super(code);
        this.code = code;
        this.status = status;
        this.errors = Map.of("error", code);
    }

    public AppException(String code, HttpStatus status, Object errors) {
        super(code);
        this.code = code;
        this.status = status;
        this.errors = errors != null ? errors : Map.of("error", code);
    }

}