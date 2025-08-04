package com.project.system.enums.input;

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

    // Retorna o valor completo com emoji + label, como no select
    public String getDisplay() {
        return emoji + " " + label;
    }

    // Método para obter enum a partir do label, útil para converter String em enum
    public static ProjectStatus fromLabel(String label) {
        for (ProjectStatus status : values()) {
            if (status.label.equalsIgnoreCase(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status desconhecido: " + label);
    }
}

