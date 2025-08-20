package com.project.system.controller.input;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.servlet.ModelAndView;

import com.project.system.entity.User;
import com.project.system.service.CommomUserService;
import com.project.system.utils.AuthenticationUtils;

@Controller
@PreAuthorize("hasRole('USER')")
public class UserController {
    
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
    
    @GetMapping("/input/user/users/profile")
    public ModelAndView profileUser(Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return commomUserService.getProfileView(loggedUser);
    }
}
