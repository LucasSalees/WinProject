package com.project.system.controller.input;

import java.util.List;
import java.util.Optional;

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
import com.project.system.service.CommomUserService;
import com.project.system.service.input.UserService;
import com.project.system.utils.AuthenticationUtils;

@Controller
@PreAuthorize("hasRole('USER')")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private CommomUserService commomUserService;
    
    @GetMapping("/input/user/home")
    @PreAuthorize("hasRole('USER')")
    public ModelAndView adminHome(Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("user/home");

        mv.addObject("LoggedUser", loggedUser);
        return mv;
    }

    @GetMapping("/input/user/users/register")
    @PreAuthorize("hasAuthority('USER_REGISTER')")
    public ModelAndView register(User user, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        ModelAndView mv = new ModelAndView("inputuser/users/register");
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("user", user);
        mv.addObject("departments", userService.getAllDepartments());
        mv.addObject("occupations", userService.getAllOccupations());
        mv.addObject("functions", userService.getAllFunctions());
        return mv;
    }

    @GetMapping("/input/user/users/list")
    @PreAuthorize("hasAnyAuthority('USER_LIST', 'USER_EDIT')")
    public ModelAndView userList(@RequestParam(value = "filter", required = false) String filter,
			Authentication authentication) {
		User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
		List<User> users;

		if (filter != null && !filter.trim().isEmpty()) {
			users = userService.searchUsers(filter);
		} else {
			users = userService.getAllUsers();
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
			users = userService.searchUsers(filter);
		} else {
			users = userService.getAllUsers();
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
		User user = userService.getUserById(userId)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

		ModelAndView mv = new ModelAndView("input/user/users/printOne");
		mv.addObject("LoggedUser", loggedUser);
		mv.addObject("users", user);
		mv.addObject("dataAtual", new java.util.Date());
		return mv;
	}

    @GetMapping("/input/user/users/edit/{userId}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ModelAndView editUser(@PathVariable("userId") Long userId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        Optional<User> userOpt = userService.getUserById(userId);

        if (userOpt.isEmpty()) {
            return new ModelAndView("redirect:/input/user/users/list");
        }
        User user = userOpt.get();

        ModelAndView mv = new ModelAndView("input/user/users/edit");
        mv.addObject("user", user);
        mv.addObject("LoggedUser", loggedUser);
        mv.addObject("allowedDays", user.getAllowedDays());
        mv.addObject("departments", userService.getAllDepartments());
        mv.addObject("occupations", userService.getAllOccupations());
        mv.addObject("functions", userService.getAllFunctions());

        return mv;
    }
    
    @GetMapping("/input/user/removeUser/{userId}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> remove(@PathVariable("userId") Long userId, Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return userService.removeUser(userId, loggedUser);
    }

    @PostMapping(value = "/input/user/users/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('USER_SAVE_EDIT')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> saveEditions(@ModelAttribute("user") User user,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "removePhoto", required = false) Boolean removePhoto,
            @RequestParam(name = "novaSenha", required = false) String newUserPassword, BindingResult result,
            Authentication authentication) {

        return userService.saveEditions(user, profileImage, removePhoto, newUserPassword);
    }

    @PostMapping(value = "/input/user/users/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('USER_REGISTER')")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> save(@ModelAttribute("user") User user,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "removePhoto", required = false) Boolean removePhoto, BindingResult result,
            Authentication authentication) {
        return userService.saveNewUser(user, profileImage, removePhoto);
    }
    
    @GetMapping("/input/user/users/profile")
    public ModelAndView profileUser(Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return commomUserService.getProfileView(loggedUser);
    }
}