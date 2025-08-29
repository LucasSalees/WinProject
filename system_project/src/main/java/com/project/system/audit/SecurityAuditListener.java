package com.project.system.audit;

import com.project.system.entity.AuditLog;
import com.project.system.exceptions.DayAccessRestrictedException;
import com.project.system.exceptions.TimeAccessRestrictedException;
import com.project.system.exceptions.UserBlockedException;
import com.project.system.security.UserDetailsImpl;
import com.project.system.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

@Component
public class SecurityAuditListener {

    @Autowired
    private AuditLogService auditLogService;

    /**
     * Escuta o evento de sucesso de autenticação.
     * Este método é chamado automaticamente pelo Spring quando um usuário faz login com sucesso.
     */
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        // Pega os detalhes do usuário que acabou de logar
        Object principal = event.getAuthentication().getPrincipal();
        String username = "N/A";
        String userId = "N/A";

        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            username = userDetails.getUser().getUserName();
            userId = userDetails.getUser().getUserId().toString();
        } else if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        String description = "Usuário '" + username + "' logou com sucesso no sistema.";

        // Cria o registro de log
        AuditLog auditLog = new AuditLog(
                "LOGIN_SUCCESS",
                "SpringSecurity",
                userId,
                description,
                null,
                null,
                "Authentication",
                userId,
                username
        );
        
        // Salva o log
        auditLogService.saveLog(auditLog);
    }

    /**
     * Escuta o evento de falha de autenticação (credenciais erradas).
     * Este método é chamado automaticamente quando uma tentativa de login falha.
     */
    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        // Pega o nome de usuário que foi tentado
        String username = (String) event.getAuthentication().getPrincipal();
        
        String description = "Tentativa de login falhou para o usuário '" + username + "'. Credenciais inválidas.";

        AuditLog auditLog = new AuditLog(
                "LOGIN_FAILURE",
                "SpringSecurity",
                null,
                description,
                null,
                null,
                "Authentication",
                "N/A", // Não há ID de usuário, pois o login falhou
                username
        );

        auditLogService.saveLog(auditLog);
    }

    /**
     * Escuta o evento de logout bem-sucedido.
     * Este método é chamado automaticamente quando o usuário clica em "Sair".
     */
    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent event) {
        // Pega os detalhes do usuário que fez logout
        Object principal = event.getAuthentication().getPrincipal();
        String username = "N/A";
        String userId = "N/A";

        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            username = userDetails.getUser().getUserName();
            userId = userDetails.getUser().getUserId().toString();
        } else if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        String description = "Usuário '" + username + "' deslogou do sistema.";

        AuditLog auditLog = new AuditLog(
                "LOGOUT_SUCCESS",
                "SpringSecurity",
                userId,
                description,
                null,
                null,
                "Authentication",
                userId,
                username
        );
        
        auditLogService.saveLog(auditLog);
    }

    /**
     * Escuta o evento de falha de autenticação para contas DESATIVADAS.
     */
    @EventListener
    public void onAuthenticationFailureDisabled(AuthenticationFailureDisabledEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        String description = "Tentativa de login falhou para o usuário '" + username + "'. Conta desativada.";

        AuditLog auditLog = new AuditLog(
                "LOGIN_FAILURE_DISABLED",
                "SpringSecurity",
                null,
                description,
                null,
                null,
                "Authentication",
                "N/A",
                username
        );
        auditLogService.saveLog(auditLog);
    }

    /**
     * Escuta o evento de falha de autenticação para contas BLOQUEADAS pelo Spring (ex: excesso de tentativas).
     */
    @EventListener
    public void onAuthenticationFailureLocked(AuthenticationFailureLockedEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        String description = "Tentativa de login falhou para o usuário '" + username + "'. Conta bloqueada.";

        AuditLog auditLog = new AuditLog(
                "LOGIN_FAILURE_LOCKED",
                "SpringSecurity",
                null,
                description,
                null,
                null,
                "Authentication",
                "N/A",
                username
        );
        auditLogService.saveLog(auditLog);
    }

    /**
     * Escuta eventos de falha de autenticação customizados (acesso por dia, hora, etc.).
     * Este listener é mais genérico para capturar exceções que não têm um evento específico do Spring.
     */
    @EventListener
    public void onCustomAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        // Ignora os eventos que já são tratados por listeners mais específicos para evitar logs duplicados.
        if (event instanceof AuthenticationFailureBadCredentialsEvent ||
            event instanceof AuthenticationFailureDisabledEvent ||
            event instanceof AuthenticationFailureLockedEvent) {
            return;
        }

        String username = (String) event.getAuthentication().getPrincipal();
        AuthenticationException exception = event.getException();
        
        // *** INÍCIO DA CORREÇÃO ***
        // Procura pela exceção original "embrulhada" para ter o motivo real da falha.
        Throwable rootCause = exception.getCause() != null ? exception.getCause() : exception;

        String action = "LOGIN_FAILURE_UNKNOWN";
        String description = "Falha de login para o usuário '" + username + "'. Motivo: " + rootCause.getMessage();

        if (rootCause instanceof UserBlockedException) {
            action = "LOGIN_FAILURE_BLOCKED";
        } else if (rootCause instanceof DayAccessRestrictedException) {
            action = "LOGIN_FAILURE_DAY_RESTRICTED";
        } else if (rootCause instanceof TimeAccessRestrictedException) {
            action = "LOGIN_FAILURE_TIME_RESTRICTED";
        }
        // *** FIM DA CORREÇÃO ***

        AuditLog auditLog = new AuditLog(
                action,
                "SpringSecurity",
                null,
                description,
                null,
                null,
                "Authentication",
                "N/A",
                username
        );
        auditLogService.saveLog(auditLog);
    }
}

