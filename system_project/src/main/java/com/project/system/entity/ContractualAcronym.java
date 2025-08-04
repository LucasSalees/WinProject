package com.project.system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ContractualAcronym {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long contractualAcronymId;
	private String contractualAcronymName;
	private String acronym;
	
	public ContractualAcronym() {
	}
	
	public ContractualAcronym(String acronym, String acronymName, Long contractualAcronymId) {
		this.acronym = acronym;
		this.contractualAcronymId = contractualAcronymId;
		this.contractualAcronymName = acronymName;
	}

	public Long getContractualAcronymId() {
		return contractualAcronymId;
	}

	public void setContractualAcronymId(Long contractualAcronymId) {
		this.contractualAcronymId = contractualAcronymId;
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

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}
	
	
}
