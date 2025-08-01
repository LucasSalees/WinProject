package com.project.system.enums.input;

public enum UserRole {

    USER("Usuário"),
    ADMIN("Administrador"),
    MANAGER("Gerente"),
    DIRECTOR("Diretor");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
    
    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}