package com.project.system.enums.input;

import java.text.Normalizer;

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

    public static ProjectPriority fromLabel(String label) {
        String normalizedLabel = Normalizer.normalize(label, Normalizer.Form.NFD)
                                         .replaceAll("[^\\p{ASCII}]", "");

        for (ProjectPriority priority : ProjectPriority.values()) {
            String enumLabelNormalized = Normalizer.normalize(priority.label, Normalizer.Form.NFD)
                                                 .replaceAll("[^\\p{ASCII}]", "");
            if (enumLabelNormalized.equalsIgnoreCase(normalizedLabel)) {
                return priority;
            }
        }
        return null;
    }
}