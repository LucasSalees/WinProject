package com.project.system.service.input;

import java.io.IOException; // IMPORTAR IOException
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.system.audit.Auditable; // IMPORTAR A ANOTAÇÃO
import com.project.system.entity.AuditLog;
import com.project.system.entity.User;
import com.project.system.enums.input.UserPermission;
import com.project.system.enums.input.UserRole;
import com.project.system.repositories.AuditLogRepository;
import com.project.system.repositories.ContractualAcronymRepository;
import com.project.system.repositories.DepartmentRepository;
import com.project.system.repositories.FunctionRepository;
import com.project.system.repositories.OccupationRepository;
import com.project.system.repositories.ProjectRepository;
import com.project.system.repositories.UserRepository;
import com.project.system.service.CommomUserService;
import com.project.system.service.FileStorageService;
import com.project.system.utils.PasswordUtils;
import com.project.system.utils.RoleValidator;

@Service
public class DirectorService {
	
	@Autowired
	private CommomUserService commomUserService;

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

    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
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

    @Auditable(action = "DELETE_USER")
    public User removeUser(Long userId, User loggedUser) {
        User userExists = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));
        
        if (loggedUser.getUserId().equals(userExists.getUserId())) {
            throw new RuntimeException("Você não pode excluir a si mesmo.");
        }
        
        userRepository.delete(userExists);
        
        return userExists;
    }

    @Auditable(action = "UPDATE_USER")
    public User saveEditions(User user, MultipartFile profileImage, Boolean removePhoto,
            String newUserPassword, User loggedUser) {

        User userExists = userRepository.findById(user.getUserId())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Optional<User> userWithSameEmail = userRepository.findByEmail(user.getUserEmail());
        if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Este e-mail já está em uso por outro usuário.");
        }
        
        try {
            RoleValidator.validateRoleChange(loggedUser, user.getUserRole(), userExists, user.getPermissions());
        } catch (SecurityException e) {
            throw new SecurityException(e.getMessage());
        }

        Set<UserPermission> allowedPermissionsForLoggedUser = loggedUser.getUserRole().getBasePermissions();
        Set<UserPermission> allowedPermissionsForTargetRole = user.getUserRole().getBasePermissions();
        
        Set<UserPermission> validPermissions = user.getPermissions().stream()
            .filter(allowedPermissionsForLoggedUser::contains)
            .filter(allowedPermissionsForTargetRole::contains)
            .collect(Collectors.toSet());

        userExists.setPermissions(validPermissions);

        try {
            if (Boolean.TRUE.equals(removePhoto)) {
                if (userExists.getPhotoPath() != null && !userExists.getPhotoPath().equals("DefaultAvatar.png")) {
                    fileStorageService.deleteFile(userExists.getPhotoPath());
                }
                userExists.setPhotoPath("DefaultAvatar.png");
            } else if (profileImage != null && !profileImage.isEmpty()) {
                if (userExists.getPhotoPath() != null && !userExists.getPhotoPath().equals("DefaultAvatar.png")) {
                    fileStorageService.deleteFile(userExists.getPhotoPath());
                }
                String fileName = fileStorageService.storeFile(profileImage);
                userExists.setPhotoPath(fileName);
            } else if (userExists.getPhotoPath() == null || userExists.getPhotoPath().isEmpty()) {
                userExists.setPhotoPath("DefaultAvatar.png");
            }
        } catch (IOException e) {
            // Converte a exceção verificada (IOException) em uma não verificada (RuntimeException)
            throw new RuntimeException("Falha na operação com o arquivo de imagem.", e);
        }

        userExists.setUserName(user.getUserName());
        userExists.setUserEmail(user.getUserEmail());
        userExists.setUserRole(user.getUserRole());
        userExists.setUserFunction(user.getUserFunction());
        userExists.setUserOccupation(user.getUserOccupation());
        userExists.setUserTel(user.getUserTel());
        userExists.setUserRegion(user.getUserRegion());
        userExists.setUserDepartment(user.getUserDepartment());
        userExists.setAllowedDays(user.getAllowedDays());
        userExists.setStartTime(user.getStartTime());
        userExists.setEndTime(user.getEndTime());
        userExists.setUserLocked(user.isUserLocked());
        userExists.setUserInactive(user.isUserInactive());
        userExists.setUserSuspended(user.isUserSuspended());
        userExists.setUserActive(user.isUserActive());

        if (newUserPassword != null && !newUserPassword.isBlank()) {
            String erroSenha = commomUserService.validarSenha(newUserPassword, newUserPassword);
            if (erroSenha != null) {
                throw new RuntimeException(erroSenha);
            }
            userExists.setUserPassword(PasswordUtils.hashPassword(newUserPassword));
        }

        return userRepository.save(userExists);
    }

    @Auditable(action = "CREATE_USER")
    public User saveNewUser(
            User user, 
            MultipartFile profileImage, 
            Boolean removePhoto, 
            Set<UserPermission> permissions, 
            User loggedUser) {
        
        userRepository.findByEmail(user.getUserEmail()).ifPresent(u -> {
            throw new RuntimeException("Este e-mail já está cadastrado.");
        });

        if (loggedUser.getUserRole().getLevel() < user.getUserRole().getLevel()) {
            throw new SecurityException("Você não pode atribuir um nível de acesso acima do seu.");
        }

        user.setPermissions(permissions != null ? permissions : Collections.emptySet());

        String senha = "Senha123@";
        user.setUserPassword(PasswordUtils.hashPassword(senha));
        user.setUserRegistrationDate(LocalDateTime.now());

        // *** INÍCIO DA CORREÇÃO ***
        try {
            if (Boolean.TRUE.equals(removePhoto)) {
                user.setPhotoPath("DefaultAvatar.png");
            } else if (profileImage != null && !profileImage.isEmpty()) {
                String fileName = fileStorageService.storeFile(profileImage);
                user.setPhotoPath(fileName);
            } else {
                user.setPhotoPath("DefaultAvatar.png");
            }
        } catch (IOException e) {
            // Converte a exceção verificada (IOException) em uma não verificada (RuntimeException)
            throw new RuntimeException("Falha ao salvar o arquivo de imagem do novo usuário.", e);
        }
        // *** FIM DA CORREÇÃO ***

        return userRepository.save(user);
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
    
    public Page<AuditLog> searchLogsPaginated(String filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (filter != null && !filter.trim().isEmpty()) {
            return auditLogRepository.searchByFilterPaginated(filter, pageable);
        } else {
            return auditLogRepository.findAll(pageable);
        }
    }
}
