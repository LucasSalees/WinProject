package com.project.system.service.input;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.project.system.dto.StandardResponseDTO;
import com.project.system.entity.Department;
import com.project.system.entity.Occupation;
import com.project.system.entity.Project;
import com.project.system.repositories.DepartmentRepository;
import com.project.system.repositories.ProjectRepository;

@Service
public class DirectorProjectService {

    private final ProjectRepository projectRepository;
    
    private final DepartmentRepository departmentRepository;

    public DirectorProjectService(ProjectRepository projectRepository, DepartmentRepository departmentRepository) {
        this.projectRepository = projectRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long projectId) {
        return projectRepository.findById(projectId);
    }

    public ResponseEntity<StandardResponseDTO> removeProject(Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if (optionalProject.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(StandardResponseDTO.error("Projeto não encontrado!"));
        }

        try {
            projectRepository.deleteById(projectId);
            return ResponseEntity.ok(StandardResponseDTO.success("Projeto removido com sucesso!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(StandardResponseDTO.error("Erro: Projeto está ligado a outras entidades."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(StandardResponseDTO.error("Erro ao remover projeto. " + e.getMessage()));
        }
    }

    public ResponseEntity<StandardResponseDTO> saveEditions(Project project, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(StandardResponseDTO.error("Erro de validação nos campos do projeto."));
        }

        Optional<Project> existingProjectOpt = projectRepository.findById(project.getProjectId());
        if (existingProjectOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(StandardResponseDTO.error("Projeto não encontrado para edição."));
        }

        Project existingProject = existingProjectOpt.get();

        existingProject.setProjectCurrentDate(LocalDate.now());

        existingProject.setProjectName(project.getProjectName());
        existingProject.setProjectContractualAcronym(project.getProjectContractualAcronym());
        existingProject.setProjectPlanningStartDate(project.getProjectPlanningStartDate());
        existingProject.setProjectPlanningEndDate(project.getProjectPlanningEndDate());
        existingProject.setProjectLocalManagerEmail(project.getProjectLocalManagerEmail());
        existingProject.setProjectLocalManagerPhone(project.getProjectLocalManagerPhone());
        existingProject.setProjectClientManager(project.getProjectClientManager());
        existingProject.setProjectClientManagerEmail(project.getProjectClientManagerEmail());
        existingProject.setProjectClientManagerPhone(project.getProjectClientManagerPhone());
        existingProject.setProjectClientAddress(project.getProjectClientAddress());
        existingProject.setProjectClientDistrict(project.getProjectClientDistrict());
        existingProject.setProjectClientCity(project.getProjectClientCity());
        existingProject.setProjectClientState(project.getProjectClientState());
        existingProject.setProjectClientZipCode(project.getProjectClientZipCode());
        existingProject.setProjectClientAddressComplement(project.getProjectClientAddressComplement());
        existingProject.setProjectClientAddressNumber(project.getProjectClientAddressNumber());
        existingProject.setProjectWebsite(project.getProjectWebsite());
        existingProject.setProjectStatus(project.getProjectStatus());
        existingProject.setProjectExecutionPercentage(project.getProjectExecutionPercentage());
        existingProject.setProjectComment(project.getProjectComment());
        existingProject.setProjectDuration(project.getProjectDuration());
        existingProject.setProjectBudget(project.getProjectBudget());
        existingProject.setProjectRegisterDate(project.getProjectRegisterDate());
        existingProject.setUserDepartment(project.getUserDepartment());

        try {
            projectRepository.save(existingProject);
            return ResponseEntity.ok(StandardResponseDTO.success("Projeto atualizado com sucesso."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(StandardResponseDTO.error("Erro ao atualizar o projeto: " + e.getMessage()));
        }
    }

    public ResponseEntity<StandardResponseDTO> saveProject(Project project, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(StandardResponseDTO.error("Erro de validação nos campos do projeto."));
        }

        try {
            if (project.getUserDepartment() != null && project.getUserDepartment().getDepartmentId() != null) {
                departmentRepository.findById(project.getUserDepartment().getDepartmentId())
                    .ifPresent(department -> project.setProjectLocalManager(department.getDepartmentManager()));
            }

            projectRepository.save(project);
            return ResponseEntity.ok(StandardResponseDTO.success("Projeto cadastrado com sucesso!"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(StandardResponseDTO.error("Erro: Um projeto com dados semelhantes já existe."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(StandardResponseDTO.error("Erro ao salvar o projeto. Por favor, tente novamente. " + e.getMessage()));
        }
    }
    
    public List<Project> searchProjects(String filter) {
        return projectRepository.searchByFilter(filter);
    }
    
}
