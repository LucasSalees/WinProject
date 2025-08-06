package com.project.system.service;

import com.project.system.entity.AuditLog;
import com.project.system.entity.User;
import com.project.system.enums.input.AuditAction;
import com.project.system.enums.input.AuditClassName;
import com.project.system.enums.input.UserRole;
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
            AuditAction.LOGIN.name(),
            AuditClassName.AUTHENTICATION.name(),
            user != null ? String.valueOf(user.getUserId()) : null,
            "Efetuou login",
            null,
            null,
            user != null && user.getUserRole() != null ? user.getUserRole().name() : UserRole.USER.name(), 
            user != null ? String.valueOf(user.getUserId()) : null,  
            user != null ? user.getUserEmail() : null
        );

        auditLogRepository.save(log);
    }

    public void logLogout(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return;

        User user = AuthenticationUtils.getLoggedUser(authentication);

        AuditLog log = new AuditLog(
                AuditAction.LOGOUT.name(),
                AuditClassName.AUTHENTICATION.name(),
                user != null ? String.valueOf(user.getUserId()) : null,
                "Efetuou logout",
                null,
                null,
                user != null && user.getUserRole() != null ? user.getUserRole().name() : UserRole.USER.name(), 
                user != null ? String.valueOf(user.getUserId()) : null,  
                user != null ? user.getUserEmail() : null
            );

        auditLogRepository.save(log);
    }
    
    public void logAudit(AuditAction action, String className, String affectedId,
        String description, String oldValue, String newValue,
        String module, String userId, String userName) {

		AuditLog log = new AuditLog(
		   action.name(),
		   className,
		   affectedId,
		   description,
		   oldValue,
		   newValue,
		   module,
		   userId,
		   userName
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
