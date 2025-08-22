package com.project.system.service.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.system.entity.ContractualAcronym;
import com.project.system.entity.Department;
import com.project.system.entity.Function;
import com.project.system.entity.Occupation;
import com.project.system.entity.Project;
import com.project.system.entity.User;
import com.project.system.enums.input.UserRole;
import com.project.system.repositories.ContractualAcronymRepository;
import com.project.system.repositories.DepartmentRepository;
import com.project.system.repositories.FunctionRepository;
import com.project.system.repositories.OccupationRepository;
import com.project.system.repositories.ProjectRepository;
import com.project.system.repositories.UserRepository;

@Service
public class UserService {
	
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private ContractualAcronymRepository contractualAcronymRepository;

    @Autowired
    private OccupationRepository occupationRepository;

    @Autowired
    private FunctionRepository functionRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    // Método paginado para pegar todas os usários
    public Page<User> getAllUsersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }
    // Método paginado para buscar com filtro
    public Page<User> searchUsersPaginated(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        List<UserRole> matchingRoles = new ArrayList<>();

        if (filter != null && !filter.trim().isEmpty()) {
            String lowerCaseFilter = filter.trim().toLowerCase();
            
            for (UserRole role : UserRole.values()) {
                if (role.getLabel().toLowerCase().contains(lowerCaseFilter)) {
                    matchingRoles.add(role);
                }
            }
        }

        return userRepository.searchByFilterPaginated(filter, pageable, matchingRoles);
    }
    
    // Método paginado para pegar todas as ocupações
    public Page<Occupation> getAllOccupationsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return occupationRepository.findAll(pageable);
    }

    // Método paginado para buscar com filtro
    public Page<Occupation> searchOccupationsPaginated(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return occupationRepository.searchByFilterPaginated(filter, pageable);
    }
    
    // Método paginado para pegar todas as funções
    public Page<Function> getAllFunctionsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return functionRepository.findAll(pageable);
    }

    // Método paginado para buscar com filtro
    public Page<Function> searchFunctionsPaginated(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return functionRepository.searchByFilterPaginated(filter, pageable);
    }
    
    // Método paginado para pegar todas os departamentos
    public Page<Department> getAllDepartmentsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return departmentRepository.findAll(pageable);
    }

    // Método paginado para buscar com filtro
    public Page<Department> searchDepartmentsPaginated(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return departmentRepository.searchByFilterPaginated(filter, pageable);
    }
    
    // Método paginado para pegar todas as siglas contratuais
    public Page<ContractualAcronym> getAllAcronymsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return contractualAcronymRepository.findAll(pageable);
    }

    // Método paginado para buscar com filtro
    public Page<ContractualAcronym> searchAcronymsPaginated(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return contractualAcronymRepository.searchByFilterPaginated(filter, pageable);
    }
    
    // Método paginado para pegar todas os projetos
    public Page<Project> getAllProjectsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return projectRepository.findAll(pageable);
    }

    // Método paginado para buscar com filtro
    public Page<Project> searchProjectsPaginated(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return projectRepository.searchByFilterPaginated(filter, pageable);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public List<?> getAllDepartments() {
        return departmentRepository.findAll();
    }
    
    public List<?> getAllAcronyms() {
        return contractualAcronymRepository.findAll();
    }

    public List<?> getAllOccupations() {
        return occupationRepository.findAll();
    }

    public List<?> getAllFunctions() {
        return functionRepository.findAll();
    }
    
    public List<?> getAllProjects() {
        return projectRepository.findAll();
    }
    
    public List<User> searchUsers(String filter) {

        List<String> roleNames = new ArrayList<>();

        for (UserRole role : UserRole.values()) {

            if (role.getLabel().toLowerCase().contains(filter.toLowerCase())) {
                roleNames.add(role.name()); 
            }
        }

        if (!roleNames.isEmpty()) {

            return userRepository.searchByFilterAndRole(filter, roleNames);
        } else {

            return userRepository.searchByFilterAndRole(filter, Collections.emptyList());
        }
    }

}
