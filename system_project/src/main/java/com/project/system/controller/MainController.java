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

import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;

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

        // Se o usuário já está autenticado, redireciona para a home.
        // Isso impede que um usuário logado acesse a página de login novamente.
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }

        // Adiciona uma mensagem específica se a sessão expirou
        if (expired != null) {
            model.addAttribute("msg", "Sua sessão expirou. Por favor, faça o login novamente.");
        }

        // Configura outras mensagens (erro, logout)
        mainService.configurarMensagemLogin(request, model, erro, logout);

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
    
    @GetMapping("/session/timeout")
    @ResponseBody
    public int getSessionTimeout(HttpSession session) {
        return session.getMaxInactiveInterval(); 
    }

    @GetMapping("/logout")
    public String logout() {
        // Redirecionamento de logout agora é tratado pelo Spring Security
        return "redirect:/login";
    }
}
