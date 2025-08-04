package com.project.system.exeptions.handlers.input;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.project.system.controller.input.AdminContractualAcronymController;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(assignableTypes = AdminContractualAcronymController.class)
public class AdminContractualAcronymExeceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(HttpServletRequest request, AccessDeniedException ex) {
        String uri = request.getRequestURI();
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        String method = request.getMethod();

        Map<String, String> response = new HashMap<>();
        response.put("status", "error");

        if (uri.contains("/input/admin/removeContractualAcronym")) {
            response.put("mensagem", "Você não tem permissão para excluir uma sigla contratual.");
        } 
        else if (uri.contains("/input/admin/contractualAcronym/edit")) {
            response.put("mensagem", "Você não tem permissão para editar uma sigla contratual.");
        }
        else if (uri.contains("/input/admin/contractualAcronym/saveContractualAcronym")) {
            response.put("mensagem", "Você não tem permissão para cadastrar uma sigla contratual.");
        }
        else {
            response.put("mensagem", "Você não tem permissão para acessar este recurso.");
        }
        if (isAjax || method.equals("POST") || method.equals("DELETE") || 
            (method.equals("GET") && uri.contains("/input/admin/removeContractualAcronym"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        ModelAndView mav = new ModelAndView("accessDenied");
        mav.addObject("mensagemErro", response.get("mensagem"));
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(HttpServletRequest request, Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");

        if (request.getRequestURI().contains("/input/admin/contractualAcronym/save")) {
            response.put("mensagem", "Erro durante o cadastro: " + ex.getMessage());
        } 
        else if (request.getRequestURI().contains("/input/admin/removeContractualAcronym")) {
            response.put("mensagem", "Erro durante a remoção: " + ex.getMessage());
        }
        else if (request.getRequestURI().contains("/input/admin/contractualAcronym/edit")) {
            response.put("mensagem", "Erro durante a edição: " + ex.getMessage());
        }
        else {
            response.put("mensagem", "Ocorreu um erro inesperado no servidor.");
        }

        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(
            HttpServletRequest request, DataIntegrityViolationException ex) {

        Map<String, String> response = new HashMap<>();
        response.put("status", "error");

        if (request.getRequestURI().contains("/input/admin/removeContractualAcronym")) {
        	response.put("mensagem", "Não é possível remover a sigla contratual, pois ela está vinculado a um ou mais usuários.");
        } else {
            response.put("mensagem", "Operação violou regras de integridade do banco de dados.");
        }

        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // HTTP 409 - Conflito
    }

} 
