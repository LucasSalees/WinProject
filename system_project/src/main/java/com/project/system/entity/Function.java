package com.project.system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "functions")
public class Function implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long functionId;
    private String functionName;
    private String functionEmail;
    private String functionTel;
    
    public Function() {}

	public Function(String functionName, String functionEmail, String functionTel) {
	    this.functionName = functionName;
	    this.functionEmail = functionEmail;
	    this.functionTel = functionTel;
	}

	public Long getFunctionId() {
		return functionId;
	}

	public void setFunctionId(Long functionId) {
	    this.functionId = functionId;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getFunctionEmail() {
		return functionEmail;
	}

	public void setFunctionEmail(String functionEmail) {
		this.functionEmail = functionEmail;
	}

	public String getFunctionTel() {
		return functionTel;
	}

	public void setFunctionTel(String functionTel) {
		this.functionTel = functionTel;
	}
}
