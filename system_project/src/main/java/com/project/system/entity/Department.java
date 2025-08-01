package com.project.system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "Departments")
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;
    private String departmentName;
    private String departmentManager;
    private String departmentEmail;
    private String departmentTel;

	public Department() {
    }

	public Department(String departmentName, String departmentManager, String departmentEmail, String departmentTel) {
	    this.departmentName = departmentName;
	    this.departmentManager = departmentManager;
	    this.departmentEmail = departmentEmail;
	    this.departmentTel = departmentTel;
	}

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentManager() {
        return departmentManager;
    }

    public void setDepartmentManager(String departmentManager) {
        this.departmentManager = departmentManager;
    }
    
    public String getDepartmentEmail() {
		return departmentEmail;
	}

	public void setDepartmentEmail(String departmentEmail) {
		this.departmentEmail = departmentEmail;
	}

	public String getDepartmentTel() {
		return departmentTel;
	}

	public void setDepartmentTel(String departmentTel) {
		this.departmentTel = departmentTel;
	}
}
