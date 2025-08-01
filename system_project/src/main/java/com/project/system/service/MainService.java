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
                                        String erro, String logout, String expired) {
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

        if (expired != null) {
            model.addAttribute("erro", "Sessão expirada, faça login novamente.");
        }
    }

    public void configurarHome(Authentication authentication, Model model) {
        User user = AuthenticationUtils.getLoggedUser(authentication);
        model.addAttribute("user", user);
        model.addAttribute("LoggedUser", user);
        model.addAttribute("sessionTimeout", 1800);

        if (user.getEndTime() != null) {
            model.addAttribute("horaFim", user.getEndTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            model.addAttribute("horaFim", "");
        }
    }
}
