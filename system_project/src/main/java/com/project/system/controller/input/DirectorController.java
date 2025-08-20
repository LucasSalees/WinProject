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
		List<User> users;

		if (filter != null && !filter.trim().isEmpty()) {
			users = directorService.searchUsers(filter);
		} else {
			users = directorService.getAllUsers();
		}

		ModelAndView mv = new ModelAndView("input/director/users/list");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("usersList", users);
		mv.addObject("filter", filter);
		return mv;
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
        return directorService.removeUser(userId, loggedUser);
    }

    @PostMapping(value = "/input/director/users/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

        return directorService.saveEditions(user, profileImage, removePhoto, newUserPassword, loggedUser);
    }

    @PostMapping("/input/director/users/save")
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

        return directorService.saveNewUser(user, profileImage, removePhoto, permissions, loggedUser); // Adicionado 'loggedUser'
    }
    
    @GetMapping("/input/director/users/profile")
    public ModelAndView profileUser(Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return commomUserService.getProfileView(loggedUser);
    }
}
