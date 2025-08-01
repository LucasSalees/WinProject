package com.project.system.service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.project.system.dto.StandardResponseDTO;
import com.project.system.entity.User;
import com.project.system.repositories.DepartmentRepository;
import com.project.system.repositories.FunctionRepository;
import com.project.system.repositories.OccupationRepository;
import com.project.system.repositories.UserRepository;
import com.project.system.utils.PasswordUtils;

@Service
public class CommomUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private OccupationRepository occupationRepository;

    @Autowired
    private FunctionRepository functionRepository;

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

    public List<?> getAllOccupations() {
        return occupationRepository.findAll();
    }

    public List<?> getAllFunctions() {
        return functionRepository.findAll();
    }

    public ResponseEntity<StandardResponseDTO> removePhotoCadastro(Boolean removePhoto, User loggedUser) {
        try {
            if (Boolean.TRUE.equals(removePhoto)) {
                return ResponseEntity.ok(StandardResponseDTO.success("Foto removida com sucesso."));
            }
            return ResponseEntity.ok(StandardResponseDTO.success("Nenhuma ação necessária."));
        } catch (AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(StandardResponseDTO.error("Erro ao remover foto."));
        }
    }

    public ResponseEntity<StandardResponseDTO> removePhoto(String fileName, Long userId, User loggedUser) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (!loggedUser.getUserRole().equals("ADMIN") && !loggedUser.getUserId().equals(userId)) {
                throw new AccessDeniedException("Você não tem permissão para modificar este perfil");
            }

            if (fileName != null && !fileName.equals("DefaultAvatar.png")) {
                fileStorageService.deleteFile(fileName);
                user.setPhotoPath("DefaultAvatar.png");
                userRepository.save(user);
            }
            return ResponseEntity.ok(StandardResponseDTO.success("Foto removida com sucesso."));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(StandardResponseDTO.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(StandardResponseDTO.error("Erro ao remover foto."));
        }
    }


    public ModelAndView getProfileView(User loggedUser) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        ModelAndView mv = new ModelAndView("input/admin/users/profile");
        mv.addObject("loggedUser", loggedUser);
        mv.addObject("user", loggedUser);
        mv.addObject("allowedDays", loggedUser.getAllowedDays());
        mv.addObject("startTime",
                loggedUser.getStartTime() != null ? loggedUser.getStartTime().format(formatter) : "");
        mv.addObject("endTime", loggedUser.getEndTime() != null ? loggedUser.getEndTime().format(formatter) : "");
        return mv;
    }


    public Map<String, Boolean> verifyEmail(String email, Long id) {
        Map<String, Boolean> response = new HashMap<>();
        Optional<User> userOpt = userRepository.findByEmail(email);
        boolean exist = userOpt.isPresent() && (id == null || !userOpt.get().getUserId().equals(id));
        response.put("existe", exist);
        return response;
    }

    public ResponseEntity<StandardResponseDTO> changeMyPassword(String senha, String confirmarSenha, User loggedUser) {
        String erroSenha = validarSenha(senha, confirmarSenha);
        if (erroSenha != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(StandardResponseDTO.error(erroSenha));
        }
        String encryptedPassword = PasswordUtils.hashPassword(senha);
        loggedUser.setUserPassword(encryptedPassword);
        loggedUser.setPrimeiroAcesso(false);
        userRepository.save(loggedUser);

        return ResponseEntity.ok(StandardResponseDTO.success("Sua senha foi alterada com sucesso!"));
    }

    public boolean checkFirstAccess(User loggedUser) {
        return loggedUser.isPrimeiroAcesso();
    }

    public String validarSenha(String senha, String confirmarSenha) {
        if (senha == null || senha.isEmpty()) {
            return "A senha não pode estar vazia!";
        }
        if (!senha.equals(confirmarSenha)) {
            return "As senhas precisam estar iguais!";
        }
        if (senha.length() < 8) {
            return "A senha não pode ter menos de 8 caracteres!";
        }
        if (senha.length() > 20) {
            return "A senha não pode ter mais que 20 caracteres!";
        }
        if (senha.matches(".*\\s.*")) {
            return "A senha não pode conter espaços!";
        }
        if (!checkPasswordStrength(senha)) {
            return "A senha precisa ser forte ou muito forte! Adicione caracteres especiais e números.";
        }
        return null;
    }

    public boolean checkPasswordStrength(String senha) {
        int score = 0;
        if (senha.matches(".*[a-z].*"))
            score++;
        if (senha.matches(".*[A-Z].*"))
            score++;
        if (senha.matches(".*[^a-zA-Z0-9].*"))
            score++;
        if (senha.matches(".*\\d.*"))
            score++;
        return score >= 3;
    }
}
