package com.project.system.enums.input;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    USER("Usuário", 1),
    MANAGER("Gerente", 2),
    ADMIN("Administrador", 3),
    DIRECTOR("Diretor", 4);

    private final String label;
    private final int level;

    UserRole(String label, int level) {
        this.label = label;
        this.level = level;
    }
    
    @JsonValue  // 👈 Diz ao Jackson para serializar pelo label
    public String getLabel() {
        return label;
    }

    public String getRoleName() {
        return "ROLE_" + this.name();
    }

    public int getLevel() {
        return level;
    }

    public boolean canAssign(UserRole targetRole) {
        return this.level >= targetRole.level;
    }
    
    public Set<UserPermission> getBasePermissions() {
        Set<UserPermission> permissions = new HashSet<>();

        if (this == USER) {
            permissions.addAll(getPermissionsByPrefix("REPORT_"));
        }
        else if (this == MANAGER) {
            permissions.addAll(getPermissionsByPrefix("USER_"));
            permissions.addAll(getPermissionsByPrefix("PROJECT_"));
            permissions.addAll(getPermissionsByPrefix("REPORT_"));
        }
        else if (this == ADMIN || this == DIRECTOR) {
            permissions.addAll(getPermissionsByPrefix("USER_"));
            permissions.addAll(getPermissionsByPrefix("DEPARTMENT_"));
            permissions.addAll(getPermissionsByPrefix("OCCUPATION_"));
            permissions.addAll(getPermissionsByPrefix("FUNCTION_"));
            permissions.addAll(getPermissionsByPrefix("PROJECT_"));
            permissions.addAll(getPermissionsByPrefix("CONTRACTUAL_"));
            permissions.addAll(getPermissionsByPrefix("REPORT_"));
            permissions.addAll(getPermissionsByPrefix("AUDIT_")); // <-- ADICIONE ESTA LINHA
        }

        return permissions;
    }
    
    private Set<UserPermission> getPermissionsByPrefix(String prefix) {
        return Arrays.stream(UserPermission.values())
                     .filter(p -> p.name().startsWith(prefix))
                     .collect(Collectors.toSet());
    }
}