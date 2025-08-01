package com.project.system.exeptions.handlers.input;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.project.system.controller.input.ManagerFunctionController;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(assignableTypes = ManagerFunctionController.class)
public class ManagerFunctionExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(HttpServletRequest request, AccessDeniedException ex) {
        String uri = request.getRequestURI();
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        String method = request.getMethod();

        Map<String, String> response = new HashMap<>();
        response.put("status", "erro");

        if (uri.contains("/input/manager/removeFunction")) {
            response.put("mensagem", "Você não tem permissão para excluir funções.");
        } 
        else if (uri.contains("/input/manager/functions/edit")) {
            response.put("mensagem", "Você não tem permissão para editar uma função.");
        }
        else if  (uri.contains("/input/manager/functions/save")) {
            response.put("mensagem", "Você não tem permissão para editar uma função.");
        }
        else {
            response.put("mensagem", "Você não tem permissão para acessar este recurso.");
        }
        if (isAjax || method.equals("POST") || method.equals("DELETE") || 
            (method.equals("GET") && uri.contains("/input/manager/removeFunction"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        ModelAndView mav = new ModelAndView("accessDenied");
        mav.addObject("mensagemErro", response.get("mensagem"));
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(HttpServletRequest request, Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "erro");

        if (request.getRequestURI().contains("/input/manager/functions/save")) {
            response.put("mensagem", "Erro ao cadastrar função: " + ex.getMessage());
        } 
        else if (request.getRequestURI().contains("/input/manager/removeFunction")) {
            response.put("mensagem", "Erro ao remover função: " + ex.getMessage());
        }
        else if (request.getRequestURI().contains("/input/manager/functions/edit")) {
            response.put("mensagem", "Erro ao editar função: " + ex.getMessage());
        }
        else {
            response.put("mensagem", "Ocorreu um erro inesperado: " + ex.getMessage());
        }

        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(
            HttpServletRequest request, DataIntegrityViolationException ex) {
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "erro");

        if (request.getRequestURI().contains("/input/manager/removeFunction")) {
            response.put("mensagem", "Não é possível remover a função, pois ela está sendo usada por um ou mais usuários.");
        } else {
            response.put("mensagem", "Operação violou regras de integridade do banco de dados.");
        }

        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // Código 409: Conflito
    }

}