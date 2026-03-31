package com.tmdt.m3_pj_final_namqd.config.message;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Messages {

    private static MessageSource messageSource;

    public Messages(MessageSource messageSource) {
        Messages.messageSource = messageSource;
    }

    public static String get(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }
}
