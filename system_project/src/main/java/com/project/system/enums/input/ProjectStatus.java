package com.project.system.enums.input;

import java.text.Normalizer;

public enum ProjectStatus {
    NOT_STARTED("Não iniciado", "🔴"),
    PLANNING("Em planejamento", "🟡"),
    IN_PROGRESS("Em execução", "🔵"),
    DELAYED("Atrasado", "🟠"),
    COMPLETED("Concluído", "🟢");

    private final String label;
    private final String emoji;

    ProjectStatus(String label, String emoji) {
        this.label = label;
        this.emoji = emoji;
    }

    public String getLabel() {
        return label;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDisplay() {
        return emoji + " " + label;
    }
    
    public static ProjectStatus fromLabel(String label) {
        String normalizedLabel = Normalizer.normalize(label, Normalizer.Form.NFD)
                                         .replaceAll("[^\\p{ASCII}]", "");

        for (ProjectStatus status : ProjectStatus.values()) {
            String enumLabelNormalized = Normalizer.normalize(status.label, Normalizer.Form.NFD)
                                                 .replaceAll("[^\\p{ASCII}]", "");
            if (enumLabelNormalized.equalsIgnoreCase(normalizedLabel)) {
                return status;
            }
        }
        return null;
    }
}