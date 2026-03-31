package com.tmdt.m3_pj_final_namqd.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final String code;

    public AppException(String code) {
        super(code);
        this.code = code;
    }
}