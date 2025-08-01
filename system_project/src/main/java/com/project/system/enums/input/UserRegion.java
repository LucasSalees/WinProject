package com.project.system.enums.input;

public enum UserRegion {

    NORDESTE("Região Nordeste"),
    NORTE("Região Norte"),
    CENTRO_OESTE("Região Centro-Oeste"),
    SUDESTE("Região Sudeste"),
    SUL("Região Sul");

    private final String label;

    UserRegion(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
