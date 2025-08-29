package com.project.system.controller.input;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.system.dto.StandardResponseDTO;
import com.project.system.entity.AuditLog;
import com.project.system.entity.User;
import com.project.system.enums.input.OccupationType;
import com.project.system.enums.input.UserPermission;
import com.project.system.security.UserDetailsImpl;
import com.project.system.service.CommomUserService;
import com.project.system.service.input.DirectorService;
import com.project.system.utils.AuthenticationUtils;
@Controller
@PreAuthorize("hasRole('DIRECTOR')")
public class DirectorController {

    @Autowired
    private DirectorService directorService;
    
    @Autowired
    private CommomUserService commomUserService;
    
    @GetMapping("/input/director/home")
    @PreAuthorize("hasRole('DIRECTOR')")
    public ModelAndView adminHome(Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("input/director/home");

        mv.addObject("LoggedUser", loggedUser);
        return mv;
    }

    @GetMapping("/input/director/users/register")
    @PreAuthorize("hasAuthority('USER_REGISTER')")
    public ModelAndView register(User user, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("/input/director/users/register");

        mv.addObject("allPermissions", UserPermission.values());

        mv.addObject("availablePermissions", UserPermission.values());
        
        mv.addObject("occupationType", OccupationType.values());

        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("user", user);
        mv.addObject("departments", directorService.getAllDepartments());
        mv.addObject("occupations", directorService.getAllOccupations());
        mv.addObject("functions", directorService.getAllFunctions());
        mv.addObject("projects", directorService.getAllProjects());
        mv.addObject("acronyms", directorService.getAllAcronyms());

        return mv;
    }

    @GetMapping("/input/director/users/list")
    @PreAuthorize("hasAnyAuthority('USER_LIST', 'USER_EDIT')")
    public ModelAndView userList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		ModelAndView mv = new ModelAndView("input/director/users/list");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("filter", filter);
		return mv;
	}
    
    @GetMapping("/input/director/users/page")
    @PreAuthorize("hasAnyAuthority('USER_LIST', 'USER_EDIT')")
    @ResponseBody
    public Page<User> usersPage(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size) {

        if (filter != null && !filter.trim().isEmpty()) {
            return directorService.searchUsersPaginated(filter, page, size);
        } else {
            return directorService.getAllUsersPaginated(page, size);
        }
    }

    @GetMapping("/input/director/users/edit/{id}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ModelAndView editUserForm(@PathVariable Long id, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("/input/director/users/edit");

        Optional<User> userOpt = directorService.getUserById(id);
        if (userOpt.isEmpty()) {

            return new ModelAndView("redirect:/error");
        }
        
        User userToEdit = userOpt.get();

        Map<UserPermission, Boolean> permissionsMap = Arrays.stream(UserPermission.values())
            .collect(Collectors.toMap(
                permission -> permission,
                permission -> userToEdit.getPermissions().contains(permission)
            ));

        mv.addObject("user", userToEdit);
        mv.addObject("userPermissions", userToEdit.getPermissions());

        mv.addObject("permissionsMap", permissionsMap);
        mv.addObject("allPermissions", UserPermission.values());
        mv.addObject("occupationType", OccupationType.values());
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("departments", directorService.getAllDepartments());
        mv.addObject("occupations", directorService.getAllOccupations());
        mv.addObject("functions", directorService.getAllFunctions());
        mv.addObject("projects", directorService.getAllProjects());
        mv.addObject("acronyms", directorService.getAllAcronyms());

        return mv;
    }

    @GetMapping("/input/director/removeUser/{userId}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> remove(@PathVariable("userId") Long userId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        try {
            // Chama o serviço, que agora retorna a entidade removida ou lança uma exceção
            directorService.removeUser(userId, loggedUser);
            // Se a operação for bem-sucedida, cria a resposta de sucesso aqui no controller
            return ResponseEntity.ok(StandardResponseDTO.success("Usuário removido com sucesso!"));
        } catch (RuntimeException e) {
            // Se o serviço lançar uma exceção (ex: usuário não encontrado), captura e cria a resposta de erro
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StandardResponseDTO.error(e.getMessage()));
        }
    }

    @PostMapping(value = "/input/director/users/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('USER_SAVE_EDIT')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveEditions(
            @ModelAttribute("user") User user,
            @RequestParam(value = "permissionsJson", required = false) String permissionsJson,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "removePhoto", required = false) Boolean removePhoto,
            @RequestParam(name = "novaSenha", required = false) String newUserPassword,
            Authentication authentication) {

        Set<UserPermission> permissions = parsePermissions(permissionsJson);
        user.setPermissions(permissions);

        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        try {
            // Chama o serviço para salvar as edições
            directorService.saveEditions(user, profileImage, removePhoto, newUserPassword, loggedUser);
            // Cria a resposta de sucesso
            return ResponseEntity.ok(StandardResponseDTO.success("Usuário atualizado com sucesso."));
        } catch (SecurityException e) {
            // Captura exceções de segurança específicas
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(StandardResponseDTO.error(e.getMessage()));
        } catch (RuntimeException e) {
            // Captura outras exceções de negócio (ex: email duplicado, falha de IO)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(StandardResponseDTO.error("Erro ao editar o usuário: " + e.getMessage()));
        }
    }

    @PostMapping("/input/director/users/save")
    @PreAuthorize("hasAuthority('USER_REGISTER')")
    @ResponseBody // Adicionado @ResponseBody para consistência
    public ResponseEntity<StandardResponseDTO> saveUser(
        @ModelAttribute User user,
        @RequestParam(value = "permissionsJson", required = false) String permissionsJson,
        @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
        @RequestParam(value = "removePhoto", required = false) Boolean removePhoto,
        Authentication authentication) {

        Set<UserPermission> permissions = parsePermissions(permissionsJson);
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

        try {
            // Chama o serviço para criar o novo usuário
            directorService.saveNewUser(user, profileImage, removePhoto, permissions, loggedUser);
            // Cria a resposta de sucesso
            return ResponseEntity.ok(StandardResponseDTO.success("Usuário cadastrado com sucesso!"));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(StandardResponseDTO.error(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(StandardResponseDTO.error("Erro ao cadastrar usuário: " + e.getMessage()));
        }
    }
    
    @GetMapping("/input/director/users/profile")
    public ModelAndView profileUser(Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return commomUserService.getProfileView(loggedUser);
    }
    
    private Set<UserPermission> parsePermissions(String permissionsJson) {
        if (permissionsJson == null || permissionsJson.isEmpty()) {
            return Collections.emptySet();
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Set<String> permissionsStr = objectMapper.readValue(permissionsJson, new TypeReference<Set<String>>() {});
            
            if (permissionsStr != null) {
                return permissionsStr.stream()
                    .map(UserPermission::valueOf)
                    .collect(Collectors.toSet());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar permissões JSON.", e);
        }
        return Collections.emptySet();
    }
    
    @GetMapping("/input/director/audits/list")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')") // Crie esta permissão se necessário
    public ModelAndView auditList(@RequestParam(value = "filter", required = false) String filter,
                                  Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("input/director/audits/list");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("filter", filter);
        return mv;
    }

    @GetMapping("/input/director/audits/page")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    @ResponseBody
    public Page<AuditLog> auditsPage(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size) {
        return directorService.searchLogsPaginated(filter, page, size);
    }
}
