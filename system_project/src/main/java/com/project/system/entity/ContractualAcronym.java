package com.project.system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ContractualAcronym {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long acronymId;
	private String contractualAcronymName;
	private String acronym;
	
	public ContractualAcronym() {
	}
	
	public ContractualAcronym(String acronym, String acronymName, Long acronymId) {
		this.acronym = acronym;
		this.acronymId = acronymId;
		this.contractualAcronymName = acronymName;
	}

	public String getContractualAcronymName() {
		return contractualAcronymName;
	}

	public void setContractualAcronymName(String acronymName) {
		this.contractualAcronymName = acronymName;
	}

	public String getAcronym() {
		return acronym;
	}

	public Long getAcronymId() {
		return acronymId;
	}

	public void setAcronymId(Long acronymId) {
		this.acronymId = acronymId;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}
	
	
}
