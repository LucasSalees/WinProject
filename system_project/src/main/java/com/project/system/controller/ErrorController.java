package com.project.system.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/accessDenied")
    public String accessDenied() {
        return "accessDenied";
    }
    
    @GetMapping("/erro")
    public String erroGenerico(HttpServletRequest request, Model model) {
        Object mensagemErro = request.getAttribute("mensagemErro");
        Object statusCode = request.getAttribute("javax.servlet.error.status_code");

        model.addAttribute("mensagemErro", mensagemErro != null
                ? mensagemErro
                : "Ocorreu um erro inesperado.");

        model.addAttribute("status", statusCode != null
                ? statusCode.toString()
                : "500");

        return "erro";
    }
}
