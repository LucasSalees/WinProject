package com.project.system.exceptions;

import org.springframework.security.core.AuthenticationException;

public class DayAccessRestrictedException extends AuthenticationException {
    public DayAccessRestrictedException(String msg) {
        super(msg);
    }
}