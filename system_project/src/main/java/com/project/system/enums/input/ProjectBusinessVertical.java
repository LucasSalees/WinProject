package com.project.system.enums.input;

public enum ProjectBusinessVertical {
	MISCELLANEOUS("Diversos"),
    EDUCATION("Educação"),
    SPORTS("Esporte"),
    CHURCH("Igreja"),
    LOGISTICS("Logística"),
    HEALTH("Saúde");

    private final String label;

    ProjectBusinessVertical(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
