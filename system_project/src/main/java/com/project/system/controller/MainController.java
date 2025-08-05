package com.project.system.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.project.system.service.AuditService;
import com.project.system.service.MainService;

@Controller
public class MainController {

    @Autowired
    private MainService mainService;
    
    @Autowired
    private AuditService auditService;

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String mostrarTelaLogin(
            @RequestParam(value = "erro", required = false) String erro,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "expired", required = false) String expired,
            HttpServletRequest request,
            Authentication authentication,
            Model model) {

        // Somente loga se realmente tiver usuário autenticado e não for erro/logout
        if (authentication != null && authentication.isAuthenticated()
                && erro == null && logout == null && expired == null) {
            auditService.logLogin(authentication);
        }

        // Configura mensagens no model para o thymeleaf
        mainService.configurarMensagemLogin(request, model, erro, logout, expired);

        return "login";
    }

    @GetMapping("/home")
    public String home(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        	
        String role = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse("");

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/input/admin/home";
        }
        
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DIRECTOR"))) {
            return "redirect:/input/director/home";
        }
        
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            return "redirect:/input/manager/home";
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
            return "redirect:/input/user/home";
        }

        return "redirect:/login"; // fallback
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response,
                         @RequestParam(required = false) Boolean expired,
                         Authentication authentication) {
        return "redirect:/login" + (expired != null && expired ? "?expired=true" : "");
    }
}
