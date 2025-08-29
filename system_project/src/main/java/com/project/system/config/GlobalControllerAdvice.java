package com.project.system.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("urlPrefix")
    public String getUrlPrefix(Authentication authentication) {
        if (authentication == null) {
            return "";
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();

            if (role.equals("ROLE_ADMIN")) {
                return "/input/admin";
            } else if (role.equals("ROLE_DIRECTOR")) {
                return "/input/director";
            } else if (role.equals("ROLE_MANAGER")) {
                return "/input/manager";
            } else if (role.equals("ROLE_USER")) {
                return "/input/user";
            }
        }

        return "";
    }
}