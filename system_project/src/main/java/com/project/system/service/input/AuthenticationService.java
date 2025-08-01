package com.project.system.service.input;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.project.system.entity.User;
import com.project.system.utils.AuthenticationUtils;

@Service
public class AuthenticationService {

    public User getAuthenticatedUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return AuthenticationUtils.getLoggedUser(authentication);
        }
        return null;
    }
}
