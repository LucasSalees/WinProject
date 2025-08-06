package com.project.system.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.project.system.entity.User;
import com.project.system.enums.input.AuditAction;
import com.project.system.enums.input.AuditClassName;
import com.project.system.enums.input.UserRole;
import com.project.system.exceptions.DayAccessRestrictedException;
import com.project.system.exceptions.TimeAccessRestrictedException;
import com.project.system.exceptions.UserBlockedException;
import com.project.system.repositories.UserRepository;
import com.project.system.service.AuditService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private AuditService auditService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        if (exception instanceof InternalAuthenticationServiceException) {
            Throwable cause = exception.getCause();
            if (cause instanceof AuthenticationException) {
                exception = (AuthenticationException) cause;
            } else if (cause != null) {
                exception = new AuthenticationServiceException("Erro interno de autenticação.");
            }
        }

        String errorKey = "padrao";
        String errorMessage = "Email ou senha inválidos.";

        if (exception instanceof LockedException) {
            errorKey = "bloqueado";
            errorMessage = "Conta bloqueada temporariamente.";
        } else if (exception instanceof DisabledException) {
            errorKey = "desativado";
            errorMessage = "Conta desativada permanentemente.";
        } else if (exception instanceof DayAccessRestrictedException) {
            errorKey = "acesso_restrito";
            errorMessage = "Acesso não permitido neste dia.";
        } else if (exception instanceof CredentialsExpiredException) {
            errorKey = "senha_expirada";
            errorMessage = "Senha expirada. Redefina sua senha.";
        } else if (exception instanceof UserBlockedException) {
            errorKey = "usuario_bloqueado";
            errorMessage = exception.getMessage();
        } else if (exception instanceof TimeAccessRestrictedException) {
            errorKey = "horario_restrito";
            errorMessage = "Acesso não permitido neste horário.";
        }

        if (exception instanceof BadCredentialsException) {
            String email = request.getParameter("username");
            Long userId = null;
            String userEmail = null;

            if (email != null) {
                var userOpt = userRepository.findByEmail(email.toLowerCase().trim());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    userId = user.getUserId();
                    userEmail = user.getUserEmail();
                }
            }

            auditService.logAudit(
                AuditAction.LOGIN_FAILED,
                AuditClassName.AUTHENTICATION.name(),
                userId != null ? String.valueOf(userId) : null,
                "email ou senha incorreto",
                null,
                null,
                UserRole.USER.name(),
                userId != null ? String.valueOf(userId) : null,
                userEmail
            );
        }

        request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", errorMessage);
        response.sendRedirect("/login?erro=" + errorKey);
    }
}
