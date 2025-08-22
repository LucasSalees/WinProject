package com.project.system.service.input;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.system.dto.StandardResponseDTO;
import com.project.system.entity.Department;
import com.project.system.repositories.DepartmentRepository;

@Service
public class ManagerDepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;
    
    // Método paginado para pegar todas as ocupações
    public Page<Department> getAllDepartmentsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return departmentRepository.findAll(pageable);
    }

    // Método paginado para buscar com filtro
    public Page<Department> searchDepartmentsPaginated(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return departmentRepository.searchByFilterPaginated(filter, pageable);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> getDepartmentById(Long departmentId) {
        return departmentRepository.findById(departmentId);
    }

    public ResponseEntity<StandardResponseDTO> removeDepartment(Long departmentId) {
        Optional<Department> departmentOpt = departmentRepository.findById(departmentId);
        if (departmentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(StandardResponseDTO.error("Departamento não encontrado!"));
        }

        departmentRepository.deleteById(departmentId);
        return ResponseEntity.ok(StandardResponseDTO.success("Departamento removido com sucesso!"));
    }

    public ResponseEntity<StandardResponseDTO> saveEditions(Department department) {
        try {
            Optional<Department> departmentExistsOpt = departmentRepository.findById(department.getDepartmentId());
            if (departmentExistsOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(StandardResponseDTO.error("Departamento não encontrado."));
            }

            Department departmentExists = departmentExistsOpt.get();

            departmentExists.setDepartmentName(department.getDepartmentName());
            departmentExists.setDepartmentManager(department.getDepartmentManager());
            departmentExists.setDepartmentEmail(department.getDepartmentEmail());
            departmentExists.setDepartmentTel(department.getDepartmentTel());

            departmentRepository.save(departmentExists);

            return ResponseEntity.ok(StandardResponseDTO.success("Departamento atualizado com sucesso."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(StandardResponseDTO.error("Erro ao editar o Departamento."));
        }
    }

    public ResponseEntity<StandardResponseDTO> saveDepartment(Department department) {
        try {
            departmentRepository.save(department);
            return ResponseEntity.ok(StandardResponseDTO.success("Departamento cadastrado com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(StandardResponseDTO.error("Erro ao cadastrar Departamento: " + e.getMessage()));
        }
    }
    
    public List<Department> searchDepartments(String filter) {
        return departmentRepository.searchByFilter(filter);
    }
}
