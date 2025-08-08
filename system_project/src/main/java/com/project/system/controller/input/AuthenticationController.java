package com.project.system.controller.input;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.project.system.entity.User;
import com.project.system.enums.input.UserRole;
import com.project.system.service.input.AuthenticationService;

@ControllerAdvice
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService; 

    @ModelAttribute
    public void addLoggedUserToModel(Authentication authentication, Model model) {
        User user = authService.getAuthenticatedUser(authentication);
        if (user != null) {
            model.addAttribute("LoggedUser", user);

            // ✅ Define o prefixo de URL baseado na role
            String prefix = 
            	    user.getUserRole() == UserRole.ADMIN ? "/input/admin" :
            	    user.getUserRole() == UserRole.DIRECTOR ? "/input/director" :
            	    user.getUserRole() == UserRole.MANAGER ? "/input/manager" :
            	    "/input/user";

            	model.addAttribute("urlPrefix", prefix);
        }
    }
}
