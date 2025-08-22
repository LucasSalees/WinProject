package com.project.system.controller.input;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.project.system.entity.User;
import com.project.system.enums.input.UserPermission;
import com.project.system.security.UserDetailsImpl;
import com.project.system.service.CommomUserService;
import com.project.system.service.input.ManagerService;
import com.project.system.utils.AuthenticationUtils;

@Controller
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {

    @Autowired
    private ManagerService managerService;
    
    @Autowired
    private CommomUserService commomUserService;
    
    @GetMapping("/input/manager/home")
    @PreAuthorize("hasRole('MANAGER')")
    public ModelAndView adminHome(Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("input/manager/home");

        mv.addObject("LoggedUser", loggedUser);
        return mv;
    }

    @GetMapping("/input/manager/users/register")
    @PreAuthorize("hasAuthority('USER_REGISTER')")
    public ModelAndView register(User user, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("input/manager/users/register");

        mv.addObject("allPermissions", UserPermission.values());

        mv.addObject("availablePermissions", UserPermission.values());

        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("user", user);
        mv.addObject("departments", managerService.getAllDepartments());
        mv.addObject("occupations", managerService.getAllOccupations());
        mv.addObject("functions", managerService.getAllFunctions());
        mv.addObject("projects", managerService.getAllProjects());
        mv.addObject("acronyms", managerService.getAllAcronyms());

        return mv;
    }

    @GetMapping("/input/manager/users/list")
    @PreAuthorize("hasAnyAuthority('USER_LIST', 'USER_EDIT')")
    public ModelAndView userList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);

		ModelAndView mv = new ModelAndView("input/manager/users/list");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("filter", filter);
		return mv;
	}
    
    @GetMapping("/input/manager/users/page")
    @PreAuthorize("hasAnyAuthority('USER_LIST', 'USER_EDIT')")
    @ResponseBody
    public Page<User> usersPage(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "50") int size) {

        if (filter != null && !filter.trim().isEmpty()) {
            return managerService.searchUsersPaginated(filter, page, size);
        } else {
            return managerService.getAllUsersPaginated(page, size);
        }
    }
    
    @GetMapping("/input/manager/users/edit/{id}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ModelAndView editUserForm(@PathVariable Long id, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("/input/manager/users/edit");

        Optional<User> userOpt = managerService.getUserById(id);
        if (userOpt.isEmpty()) {

            return new ModelAndView("redirect:/error");
        }
        
        User userToEdit = userOpt.get();

        // Cria mapa de permissões
        Map<UserPermission, Boolean> permissionsMap = Arrays.stream(UserPermission.values())
            .collect(Collectors.toMap(
                permission -> permission,
                permission -> userToEdit.getPermissions().contains(permission)
            ));

        mv.addObject("user", userToEdit);
        mv.addObject("userPermissions", userToEdit.getPermissions());

        mv.addObject("permissionsMap", permissionsMap);
        mv.addObject("allPermissions", UserPermission.values());
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("departments", managerService.getAllDepartments());
        mv.addObject("occupations", managerService.getAllOccupations());
        mv.addObject("functions", managerService.getAllFunctions());
        mv.addObject("projects", managerService.getAllProjects());
        mv.addObject("acronyms", managerService.getAllAcronyms());

        return mv;
    }

    @GetMapping("/input/manager/removeUser/{userId}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> remove(@PathVariable("userId") Long userId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return managerService.removeUser(userId, loggedUser);
    }

    @PostMapping(value = "/input/manager/users/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority(\'USER_SAVE_EDIT\')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveEditions(
            @ModelAttribute("user") User user,
            @RequestParam(value = "permissionsJson", required = false) String permissionsJson,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "removePhoto", required = false) Boolean removePhoto,
            @RequestParam(name = "novaSenha", required = false) String newUserPassword,
            BindingResult result,
            Authentication authentication) {

        Set<UserPermission> permissions = Collections.emptySet();
        if (permissionsJson != null && !permissionsJson.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Set<String> permissionsStr = objectMapper.readValue(permissionsJson, new TypeReference<Set<String>>() {});
                
                if (permissionsStr != null) {
                    permissions = permissionsStr.stream()
                        .map(UserPermission::valueOf)
                        .collect(Collectors.toSet());
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new StandardResponseDTO("Erro ao processar permissões.", "error"));
            }
        }
        
        user.setPermissions(permissions);

        // --- CORREÇÃO APLICADA AQUI ---
        // 1. Obtém o objeto principal (que é do tipo UserDetailsImpl)
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // 2. Extrai a entidade User a partir dele (assumindo que há um método .getUser() em UserDetailsImpl)
        User loggedUser = userDetails.getUser();

        return managerService.saveEditions(user, profileImage, removePhoto, newUserPassword, loggedUser);
    }

    @PostMapping("/input/manager/users/save")
    @PreAuthorize("hasAuthority(\'USER_REGISTER\')")
    public ResponseEntity<?> saveUser(
        @ModelAttribute User user,
        @RequestParam(value = "permissionsJson", required = false) String permissionsJson,
        @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
        @RequestParam(value = "removePhoto", required = false) Boolean removePhoto,
        Authentication authentication) {

        Set<UserPermission> permissions = Collections.emptySet();
        if (permissionsJson != null && !permissionsJson.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Set<String> permissionsStr = objectMapper.readValue(permissionsJson, new TypeReference<Set<String>>() {});

                if (permissionsStr != null) {
                    permissions = permissionsStr.stream()
                        .map(UserPermission::valueOf)
                        .collect(Collectors.toSet());
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new StandardResponseDTO("Erro ao processar permissões.", "error"));
            }
        }
        
        // --- CORREÇÃO APLICADA AQUI ---
        // Obtém o objeto principal (que é do tipo UserDetailsImpl)
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // Extrai a entidade User a partir dele
        User loggedUser = userDetails.getUser();

        return managerService.saveNewUser(user, profileImage, removePhoto, permissions, loggedUser); // Adicionado 'loggedUser'
    }
    
    @GetMapping("/input/manager/users/profile")
    public ModelAndView profileUser(Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return commomUserService.getProfileView(loggedUser);
    }
}
