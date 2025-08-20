package com.project.system.service.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
        // 1. Cria uma lista para armazenar os nomes dos enums
        List<String> roleNames = new ArrayList<>();

        // 2. Itera sobre todos os enums de UserRole
        for (UserRole role : UserRole.values()) {
            // 3. Se a label do enum contém o filtro, adicione o nome do enum à lista
            if (role.getLabel().toLowerCase().contains(filter.toLowerCase())) {
                roleNames.add(role.name()); 
            }
        }
        
        // 4. Se a lista de nomes de enums não estiver vazia, use a nova query
        if (!roleNames.isEmpty()) {
            // Chama o novo método do repositório
            return userRepository.searchByFilterAndRole(filter, roleNames);
        } else {
            // 5. Se não houver correspondência, faça a pesquisa padrão sem o filtro de role
            // Aqui, você pode usar a query que já existe, mas passando uma lista vazia,
            // ou pode criar uma nova query no repositório que não tenha o critério de role
            
            // A melhor forma é usar a mesma query, mas passando o filtro de role vazio.
            // Para isso, a sua query no repositório precisa ser ajustada para aceitar a lista vazia.
            // O Spring Data JPA já lida bem com a cláusula IN com uma lista vazia,
            // fazendo com que o critério de busca seja ignorado.
            return userRepository.searchByFilterAndRole(filter, Collections.emptyList());
        }
    }

}
