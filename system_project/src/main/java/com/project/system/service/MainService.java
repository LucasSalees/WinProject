package com.project.system.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.project.system.entity.User;
import com.project.system.utils.AuthenticationUtils;

import java.time.format.DateTimeFormatter;

@Service
public class MainService {

    public void configurarMensagemLogin(HttpServletRequest request, Model model,
                                        String erro, String logout) {
        String errorMessage = (String) request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

        if (errorMessage != null) {
            model.addAttribute("erro", errorMessage);
            request.getSession().removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        } else if (erro != null) {
            model.addAttribute("erro", "Email ou senha inválidos.");
        }

        if (logout != null) {
            model.addAttribute("msg", "Logout realizado com sucesso.");
        }
    }

    public void configurarHome(HttpServletRequest request, Authentication authentication, Model model) {
        User user = AuthenticationUtils.getLoggedUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("LoggedUser", user);

        // Pega o timeout real configurado no application.properties
        int sessionTimeout = request.getSession().getMaxInactiveInterval();
        model.addAttribute("sessionTimeout", sessionTimeout);

        if (user.getEndTime() != null) {
            model.addAttribute("horaFim", user.getEndTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            model.addAttribute("horaFim", "");
        }
    }
}
