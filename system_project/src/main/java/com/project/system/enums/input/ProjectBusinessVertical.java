// src/main/java/com/project/system/enums/input/ProjectBusinessVertical.java

package com.project.system.enums.input;

import java.text.Normalizer;

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
    
    public static ProjectBusinessVertical fromLabel(String label) {
        String normalizedLabel = Normalizer.normalize(label, Normalizer.Form.NFD)
                                         .replaceAll("[^\\p{ASCII}]", "");

        for (ProjectBusinessVertical vertical : ProjectBusinessVertical.values()) {
            String enumLabelNormalized = Normalizer.normalize(vertical.label, Normalizer.Form.NFD)
                                                 .replaceAll("[^\\p{ASCII}]", "");
            if (enumLabelNormalized.equalsIgnoreCase(normalizedLabel)) {
                return vertical;
            }
        }
        return null;
    }
}