package com.project.system.enums.input;

public enum ProjectPriority {
    LOW("Baixo"),
    MEDIUM("Médio"),
    HIGH("Alto"),
    CRITICAL("Crítico");

    private final String label;

    ProjectPriority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
