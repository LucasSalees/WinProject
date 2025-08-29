package com.project.system.enums.input;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OccupationType {
    FAMILY("Família"),
    OCCUPATION("Ocupação"),
    SYNONYMOUS("Sinônimos");

    private final String label;

    OccupationType(String label) {
        this.label = label;
    }

    @JsonValue // 👈 garante que o JSON enviado para o frontend seja o label em português
    public String getLabel() {
        return label;
    }
}
