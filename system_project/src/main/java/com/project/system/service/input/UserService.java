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
    
    public Page<User> getAllUsersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

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
