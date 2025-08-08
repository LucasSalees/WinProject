package com.project.system.service.input;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.system.dto.StandardResponseDTO;
import com.project.system.entity.User;
import com.project.system.enums.input.UserPermission;
import com.project.system.repositories.ContractualAcronymRepository;
import com.project.system.repositories.DepartmentRepository;
import com.project.system.repositories.FunctionRepository;
import com.project.system.repositories.OccupationRepository;
import com.project.system.repositories.ProjectRepository;
import com.project.system.repositories.UserRepository;
import com.project.system.service.CommomUserService;
import com.project.system.service.FileStorageService;
import com.project.system.utils.PasswordUtils;

@Service
public class AdminService {
	
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

    public ResponseEntity<StandardResponseDTO> removeUser(Long userId, User loggedUser) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(StandardResponseDTO.error("Usuário não encontrado!"));
        }
        User userExists = userOpt.get();
        if (loggedUser.getUserId().equals(userExists.getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StandardResponseDTO.error("Você não pode excluir a si mesmo."));
        }
        userRepository.deleteById(userId);
        return ResponseEntity.ok(StandardResponseDTO.success("Usuário removido com sucesso!"));
    }

    public ResponseEntity<StandardResponseDTO> saveEditions(User user, MultipartFile profileImage, Boolean removePhoto,
            String newUserPassword) {

        try {
            Optional<User> userExistsOpt = userRepository.findById(user.getUserId());
            if (userExistsOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(StandardResponseDTO.error("Usuário não encontrado."));
            }

            Optional<User> userWithSameEmail = userRepository.findByEmail(user.getUserEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getUserId().equals(user.getUserId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(StandardResponseDTO.error("Este e-mail já está em uso por outro usuário."));
            }

            User userExists = userExistsOpt.get();

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
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                                         .body(StandardResponseDTO.error(erroSenha));
                }
                userExists.setUserPassword(PasswordUtils.hashPassword(newUserPassword));
            }
            
            userExists.setPermissions(user.getPermissions() != null ? user.getPermissions() : Collections.emptySet());

            userRepository.save(userExists);

            return ResponseEntity.ok(StandardResponseDTO.success("Usuário atualizado com sucesso."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(StandardResponseDTO.error("Erro ao editar o usuário." + e.getMessage()));
        }
    }

    public ResponseEntity<StandardResponseDTO> saveNewUser(
            User user, 
            MultipartFile profileImage, 
            Boolean removePhoto, 
            Set<UserPermission> permissions) {
        try {
            Optional<User> emailOwner = userRepository.findByEmail(user.getUserEmail());
            if (emailOwner.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(StandardResponseDTO.error("Este e-mail já está cadastrado."));
            }

            // Define as permissões no objeto 'user' antes de salvar
            user.setPermissions(permissions != null ? permissions : Collections.emptySet());

            String senha = "Senha123@";
            user.setUserPassword(PasswordUtils.hashPassword(senha));
            user.setUserRegistrationDate(LocalDateTime.now());

            if (Boolean.TRUE.equals(removePhoto)) {
                user.setPhotoPath("DefaultAvatar.png");
            } else if (profileImage != null && !profileImage.isEmpty()) {
                String fileName = fileStorageService.storeFile(profileImage);
                user.setPhotoPath(fileName);
            } else {
                user.setPhotoPath("DefaultAvatar.png");
            }

            userRepository.save(user);

            return ResponseEntity.ok(StandardResponseDTO.success("Usuário cadastrado com sucesso!"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(StandardResponseDTO.error("Erro ao cadastrar usuário: " + e.getMessage()));
        }
    }

    public List<User> searchUsers(String filter) {
        return userRepository.searchByFilter(filter);
    }

}
