package com.project.system.utils;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import com.project.system.entity.User;
import com.project.system.security.UserDetailsImpl;

public class AuthenticationUtils {

    public static User getLoggedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Acesso negado: sessão inválida ou expirada.");
        }

        Object main = authentication.getPrincipal();

        if (main instanceof UserDetailsImpl userDetails) {
            return userDetails.getUser();
        }

        throw new AccessDeniedException("Acesso negado: não foi possível identificar o usuário autenticado.");
    }
}