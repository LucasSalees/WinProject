package com.project.system.controller;

import com.project.system.entity.AuditLog;
import com.project.system.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuditLogController {

    @Autowired
    private AuditService auditLogService;

    @GetMapping("/auditlogs")
    public String listAuditLogs(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) String userName,
                                @RequestParam(required = false) String action) {

        Page<AuditLog> logs;

        if (userName != null && !userName.isEmpty()) {
            logs = auditLogService.listByUserName(userName, page, size);
        } else if (action != null && !action.isEmpty()) {
            logs = auditLogService.listByAction(action, page, size);
        } else {
            logs = auditLogService.listAll(page, size);
        }

        model.addAttribute("logs", logs);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("userName", userName);
        model.addAttribute("action", action);

        return "auditlogs/list"; // Página Thymeleaf para mostrar logs
    }
}
