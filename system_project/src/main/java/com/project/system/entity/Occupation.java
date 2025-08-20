package com.project.system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "occupations")  // nome da tabela no plural, lowercase (boa prática)
public class Occupation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long occupationId;
    
    private String occupationName;
    private String occupationCBO;

    public Occupation() { }

    public Occupation(String occupationName, String occupationCBO) {
        this.occupationName = occupationName;
        this.occupationCBO = occupationCBO;
    }

    public String getOccupationCBO() {
		return occupationCBO;
	}

	public void setOccupationCBO(String occupationCBO) {
		this.occupationCBO = occupationCBO;
	}

	public Long getOccupationId() {
        return occupationId;
    }

    public void setOccupationId(Long occupationId) {
        this.occupationId = occupationId;
    }

    public String getOccupationName() {
        return occupationName;
    }

    public void setOccupationName(String occupationName) {
        this.occupationName = occupationName;
    }

    @Override
    public String toString() {
        return "Occupation{" +
                "occupationId=" + occupationId +
                ", occupationName='" + occupationName + '}';
    }
}
