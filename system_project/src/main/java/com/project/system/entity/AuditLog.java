package com.project.system.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "AuditLogs")
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long auditId;

    private String action;
    private String className;
    private String module;
    private String acftedId;
    private String description;

    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Column(columnDefinition = "TEXT")
    private String newValue;
    
    private LocalDateTime timestamp;

    private String userId;
    private String userName;

    public AuditLog() {}

    public AuditLog(String action, String className, String acftedId, String description,
                    String oldValue, String newValue,  String module,
                    String userId, String userName) {
        this.action = action;
        this.className = className;
        this.module = module;
        this.acftedId = acftedId;
        this.description = description;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.userId = userId;
        this.userName = userName;
        this.timestamp = LocalDateTime.now();
    }

	@PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    // Getters e Setters

    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAcftedId() {
        return acftedId;
    }

    public void setAcftedId(String acftedId) {
        this.acftedId = acftedId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}
}
