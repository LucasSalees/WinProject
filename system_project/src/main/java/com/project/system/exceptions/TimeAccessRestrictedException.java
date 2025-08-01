package com.project.system.exceptions;

import org.springframework.security.core.AuthenticationException;

public class TimeAccessRestrictedException extends AuthenticationException {
    public TimeAccessRestrictedException(String msg) {
        super(msg);
    }
}