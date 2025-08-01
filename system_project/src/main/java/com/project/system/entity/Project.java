package com.project.system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "projects")
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String projectName;
    private String projectContractualAcronym;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectCurrentDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectRegisterDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectPlanningStartDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate projectPlanningEndDate;

    private String projectLocalManager;
    private String projectLocalManagerEmail;
    private String projectLocalManagerPhone;

    private String projectClientManager;
    private String projectClientManagerEmail;
    private String projectClientManagerPhone;
    private String projectClientAddress;
    private String projectClientDistrict;
    private String projectClientCity;
    private String projectClientState;
    private String projectClientZipCode;
    private String projectClientAddressComplement;
    private String projectClientAddressNumber;
    
    private String projectWebsite;
    private String projectStatus;
    private Byte projectExecutionPercentage;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String projectComment;
    private String projectBusinessVertical;
    private String projectPriority;
    private String projectDuration;
    private String projectBudget;
    
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "departmentId")
    private Department userDepartment;

    public Long getProjectId() {
        return projectId;	
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectContractualAcronym() {
        return projectContractualAcronym;
    }

    public void setProjectContractualAcronym(String projectContractualAcronym) {
        this.projectContractualAcronym = projectContractualAcronym;
    }

    public LocalDate getProjectPlanningStartDate() {
        return projectPlanningStartDate;
    }

    public void setProjectPlanningStartDate(LocalDate projectPlanningStartDate) {
        this.projectPlanningStartDate = projectPlanningStartDate;
    }

    public LocalDate getProjectPlanningEndDate() {
        return projectPlanningEndDate;
    }

    public void setProjectPlanningEndDate(LocalDate projectPlanningEndDate) {
        this.projectPlanningEndDate = projectPlanningEndDate;
    }

    public String getProjectLocalManagerEmail() {
        return projectLocalManagerEmail;
    }

    public void setProjectLocalManagerEmail(String projectLocalManagerEmail) {
        this.projectLocalManagerEmail = projectLocalManagerEmail;
    }

    public String getProjectLocalManagerPhone() {
        return projectLocalManagerPhone;
    }

    public void setProjectLocalManagerPhone(String projectLocalManagerPhone) {
        this.projectLocalManagerPhone = projectLocalManagerPhone;
    }

    public String getProjectClientManager() {
        return projectClientManager;
    }

    public void setProjectClientManager(String projectClientManager) {
        this.projectClientManager = projectClientManager;
    }

    public String getProjectClientManagerEmail() {
        return projectClientManagerEmail;
    }

    public void setProjectClientManagerEmail(String projectClientManagerEmail) {
        this.projectClientManagerEmail = projectClientManagerEmail;
    }

    public String getProjectClientManagerPhone() {
        return projectClientManagerPhone;
    }

    public void setProjectClientManagerPhone(String projectClientManagerPhone) {
        this.projectClientManagerPhone = projectClientManagerPhone;
    }

    public String getProjectClientAddress() {
        return projectClientAddress;
    }

    public void setProjectClientAddress(String projectClientAddress) {
        this.projectClientAddress = projectClientAddress;
    }

    public String getProjectClientDistrict() {
        return projectClientDistrict;
    }

    public void setProjectClientDistrict(String projectClientDistrict) {
        this.projectClientDistrict = projectClientDistrict;
    }

    public String getProjectClientCity() {
        return projectClientCity;
    }

    public void setProjectClientCity(String projectClientCity) {
        this.projectClientCity = projectClientCity;
    }

    public String getProjectClientState() {
        return projectClientState;
    }

    public void setProjectClientState(String projectClientState) {
        this.projectClientState = projectClientState;
    }

    public String getProjectClientZipCode() {
        return projectClientZipCode;
    }

    public void setProjectClientZipCode(String projectClientZipCode) {
        this.projectClientZipCode = projectClientZipCode;
    }

    public String getProjectClientAddressComplement() {
        return projectClientAddressComplement;
    }

    public void setProjectClientAddressComplement(String projectClientAddressComplement) {
        this.projectClientAddressComplement = projectClientAddressComplement;
    }

    public String getProjectClientAddressNumber() {
        return projectClientAddressNumber;
    }

    public void setProjectClientAddressNumber(String projectClientAddressNumber) {
        this.projectClientAddressNumber = projectClientAddressNumber;
    }

    public String getProjectWebsite() {
        return projectWebsite;
    }

    public void setProjectWebsite(String projectWebsite) {
        this.projectWebsite = projectWebsite;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public Byte getProjectExecutionPercentage() {
        return projectExecutionPercentage;
    }

    public void setProjectExecutionPercentage(Byte projectExecutionPercentage) {
        this.projectExecutionPercentage = projectExecutionPercentage;
    }

    public String getProjectComment() {
        return projectComment;
    }

    public void setProjectComment(String projectComment) {
        this.projectComment = projectComment;
    }

    public LocalDate getProjectCurrentDate() {
		return projectCurrentDate;
	}

	public void setProjectCurrentDate(LocalDate projectCurrentDate) {
		this.projectCurrentDate = projectCurrentDate;
	}
	
    public String getProjectBusinessVertical() {
		return projectBusinessVertical;
	}

	public void setProjectBusinessVertical(String projectBusinessVertical) {
		this.projectBusinessVertical = projectBusinessVertical;
	}
	
	public String getProjectPriority() {
		return projectPriority;
	}

	public void setProjectPriority(String projectPriority) {
		this.projectPriority = projectPriority;
	}
	

	public String getProjectDuration() {
		return projectDuration;
	}

	public void setProjectDuration(String projectDuration) {
		this.projectDuration = projectDuration;
	}

	public String getProjectBudget() {
		return projectBudget;
	}

	public void setProjectBudget(String projectBudget) {
		this.projectBudget = projectBudget;
	}
	
    public Department getUserDepartment() {
        return userDepartment;
    }

    public void setUserDepartment(Department Userdepartment) {
        this.userDepartment = Userdepartment;
    }
    

    public String getProjectLocalManager() {
        return projectLocalManager;
    }

    public void setProjectLocalManager(String projectLocalManager) {
        this.projectLocalManager = projectLocalManager;
    }
    

	public LocalDate getProjectRegisterDate() {
		return projectRegisterDate;
	}

	public void setProjectRegisterDate(LocalDate projectRegisterDate) {
		this.projectRegisterDate = projectRegisterDate;
	}

}