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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.system.dto.StandardResponseDTO;
import com.project.system.entity.User;
import com.project.system.enums.input.UserPermission;
import com.project.system.service.CommomUserService;
import com.project.system.service.input.UserService;
import com.project.system.utils.AuthenticationUtils;

@Controller
@PreAuthorize("hasRole('USER')")
public class UserController {

    @Autowired
    private UserService service;
    
    @Autowired
    private CommomUserService commomUserService;
    
    @GetMapping("/input/user/home")
    @PreAuthorize("hasRole('USER')")
    public ModelAndView adminHome(Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("/input/admin/home");

        mv.addObject("LoggedUser", loggedUser);
        return mv;
    }

    @GetMapping("/input/user/users/register")
    @PreAuthorize("hasAuthority('USER_REGISTER')")
    public ModelAndView register(User user, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("input/user/users/register");

        // Envia todas as permissões (se quiser exibir todas)
        mv.addObject("allPermissions", UserPermission.values());

        // Como user.getRole() pode estar null nesse ponto, envie todas ou deixe que o Thymeleaf filtre
        mv.addObject("availablePermissions", UserPermission.values());

        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("user", user);
        mv.addObject("departments", service.getAllDepartments());
        mv.addObject("occupations", service.getAllOccupations());
        mv.addObject("functions", service.getAllFunctions());
        mv.addObject("projects", service.getAllProjects());
        mv.addObject("acronyms", service.getAllAcronyms());

        return mv;
    }

    @GetMapping("/input/user/users/list")
    @PreAuthorize("hasAnyAuthority('USER_LIST', 'USER_EDIT')")
    public ModelAndView userList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<User> users;

		if (filter != null && !filter.trim().isEmpty()) {
			users = service.searchUsers(filter);
		} else {
			users = service.getAllUsers();
		}

		ModelAndView mv = new ModelAndView("input/user/users/list");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("usersList", users);
		mv.addObject("filter", filter); // devolve o filtro para manter no input
		return mv;
	}
    
    @GetMapping("/input/user/users/print")
	@PreAuthorize("hasAuthority('USER_LIST')")
	public ModelAndView printUsers(@RequestParam(required = false) String filter, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<User> users;

		if (filter != null && !filter.isEmpty()) {
			users = service.searchUsers(filter);
		} else {
			users = service.getAllUsers();
		}

		ModelAndView mv = new ModelAndView("input/user/users/print");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("usersList", users);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

	@GetMapping("/input/user/users/print/{userId}")
	@PreAuthorize("hasAuthority('USER_LIST')")
	public ModelAndView printUser(@PathVariable Long userId, Authentication authentication) {

		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		User user = service.getUserById(userId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		ModelAndView mv = new ModelAndView("input/user/users/printOne");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("users", user);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

    @GetMapping("/input/user/users/edit/{id}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ModelAndView editUserForm(@PathVariable Long id, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("input/user/users/edit");

        Optional<User> userOpt = service.getUserById(id);
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
        mv.addObject("departments", service.getAllDepartments());
        mv.addObject("occupations", service.getAllOccupations());
        mv.addObject("functions", service.getAllFunctions());
        mv.addObject("projects", service.getAllProjects());
        mv.addObject("acronyms", service.getAllAcronyms());

        return mv;
    }

    @GetMapping("/input/user/removeUser/{userId}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> remove(@PathVariable("userId") Long userId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return service.removeUser(userId, loggedUser);
    }

    @PostMapping(value = "/input/user/users/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

        return service.saveEditions(user, profileImage, removePhoto, newUserPassword);
    }

    @PostMapping("/input/user/users/save")
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

        // Chame o service passando as permissões
        return service.saveNewUser(user, profileImage, removePhoto, permissions); // Alterado aqui
    }
    
    @GetMapping("/input/user/users/profile")
    public ModelAndView profileUser(Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return commomUserService.getProfileView(loggedUser);
    }
}
