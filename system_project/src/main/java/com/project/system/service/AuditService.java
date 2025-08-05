package com.project.system.service;

import com.project.system.entity.AuditLog;
import com.project.system.entity.User;
import com.project.system.repositories.AuditLogRepository;
import com.project.system.utils.AuthenticationUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logLogin(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) return;

        User user = AuthenticationUtils.getLoggedUser(authentication);

        AuditLog log = new AuditLog(
                "LOGIN",
                "Authentication",
                String.valueOf(user.getUserId()),
                "Efetuou login",
                null,
                null,
                "Authentication",
                String.valueOf(user.getUserId()),
                user.getUserName()
        );

        auditLogRepository.save(log);

    }

    public void logLogout(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return;

        User user = AuthenticationUtils.getLoggedUser(authentication);

        AuditLog log = new AuditLog(
                "LOGOUT",
                "Authentication",
                String.valueOf(user.getUserId()),
                "Efetuou logout",
                null,
                null,
                "Authentication",
                String.valueOf(user.getUserId()),
                user.getUserName()
        );

        auditLogRepository.save(log);
    }

    // Paginação e filtros para a listagem

    public Page<AuditLog> listAll(int page, int size) {
        return auditLogRepository.findAll(PageRequest.of(page, size));
    }

    public Page<AuditLog> listByUserName(String userName, int page, int size) {
        return auditLogRepository.findByUserNameContainingIgnoreCase(userName, PageRequest.of(page, size));
    }

    public Page<AuditLog> listByAction(String action, int page, int size) {
        return auditLogRepository.findByActionContainingIgnoreCase(action, PageRequest.of(page, size));
    }
}
