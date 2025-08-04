package com.project.system.controller.input;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.project.system.dto.StandardResponseDTO;
import com.project.system.entity.User;
import com.project.system.enums.input.UserPermission;
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

        // Envia todas as permissões (se quiser exibir todas)
        mv.addObject("allPermissions", UserPermission.values());

        // Como user.getRole() pode estar null nesse ponto, envie todas ou deixe que o Thymeleaf filtre
        mv.addObject("availablePermissions", UserPermission.values());

        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("user", user);
        mv.addObject("departments", managerService.getAllDepartments());
        mv.addObject("occupations", managerService.getAllOccupations());
        mv.addObject("functions", managerService.getAllFunctions());

        return mv;
    }

    @GetMapping("/input/manager/users/list")
    @PreAuthorize("hasAnyAuthority('USER_LIST', 'USER_EDIT')")
    public ModelAndView userList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<User> users;

		if (filter != null && !filter.trim().isEmpty()) {
			users = managerService.searchUsers(filter);
		} else {
			users = managerService.getAllUsers();
		}

		ModelAndView mv = new ModelAndView("input/manager/users/list");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("usersList", users);
		mv.addObject("filter", filter); // devolve o filtro para manter no input
		return mv;
	}
    
    @GetMapping("/input/manager/users/print")
	@PreAuthorize("hasAuthority('USER_LIST')")
	public ModelAndView printUsers(@RequestParam(required = false) String filter, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<User> users;

		if (filter != null && !filter.isEmpty()) {
			users = managerService.searchUsers(filter);
		} else {
			users = managerService.getAllUsers();
		}

		ModelAndView mv = new ModelAndView("input/manager/users/print");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("usersList", users);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	@GetMapping("/input/manager/users/print/{userId}")
	@PreAuthorize("hasAuthority('USER_LIST')")
	public ModelAndView printUser(@PathVariable Long userId, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		User user = managerService.getUserById(userId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		ModelAndView mv = new ModelAndView("input/manager/users/printOne");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("users", user);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

    @GetMapping("/input/manager/users/edit/{id}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ModelAndView editUserForm(@PathVariable Long id, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("/input/manager/users/edit");

        Optional<User> userOpt = managerService.getUserById(id);
        if (userOpt.isEmpty()) {
            // redirecionar ou tratar o erro adequadamente
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
    @PreAuthorize("hasAuthority('USER_SAVE_EDIT')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveEditions(
            @ModelAttribute("user") User user,
            @RequestParam(value = "permissions", required = false) Set<String> permissionsStr,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "removePhoto", required = false) Boolean removePhoto,
            @RequestParam(name = "novaSenha", required = false) String newUserPassword,
            BindingResult result,
            Authentication authentication) {

        Set<UserPermission> permissions = Collections.emptySet();
        if (permissionsStr != null) {
            permissions = permissionsStr.stream()
                .map(UserPermission::valueOf)
                .collect(Collectors.toSet());
        }
        user.setPermissions(permissions);

        return managerService.saveEditions(user, profileImage, removePhoto, newUserPassword);
    }

    @PostMapping("/input/manager/users/save")
    @PreAuthorize("hasAuthority('USER_REGISTER')")
    public ResponseEntity<?> saveUser(
        @ModelAttribute User user,
        @RequestParam(value = "permissions", required = false) Set<String> permissionsStr,
        @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
        @RequestParam(value = "removePhoto", required = false) Boolean removePhoto) {

        Set<UserPermission> permissions = Collections.emptySet();
        if (permissionsStr != null) {
            permissions = permissionsStr.stream()
                .map(UserPermission::valueOf)
                .collect(Collectors.toSet());
        }
        user.setPermissions(permissions);

        if (user.getUserId() == null) {
            // Novo usuário
            return managerService.saveNewUser(user, profileImage, removePhoto);
        } else {
            // Edição
            return managerService.saveEditions(user, profileImage, removePhoto, null);
        }
    }
    
    @GetMapping("/input/manager/users/profile")
    public ModelAndView profileUser(Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return commomUserService.getProfileView(loggedUser);
    }
}
