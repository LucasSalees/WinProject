package com.project.system.service;

import com.project.system.entity.AuditLog;
import com.project.system.repositories.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    /**
     * Salva um log de auditoria.
     * A anotação @Transactional com Propagation.REQUIRES_NEW garante que a auditoria
     * seja salva em uma transação separada. Se a transação principal falhar (der rollback),
     * o log de auditoria da tentativa ainda será salvo.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(AuditLog auditLog) {
        auditLogRepository.save(auditLog);
    }
}