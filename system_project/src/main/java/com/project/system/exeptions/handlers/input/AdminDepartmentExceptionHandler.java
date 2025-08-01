package com.project.system.exeptions.handlers.input;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.project.system.controller.input.AdminDepartmentController;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(assignableTypes = AdminDepartmentController.class)
public class AdminDepartmentExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(HttpServletRequest request, AccessDeniedException ex) {
        String uri = request.getRequestURI();
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        String method = request.getMethod();

        Map<String, String> response = new HashMap<>();
        response.put("status", "error");

        if (uri.contains("/input/admin/removeDepartment")) {
            response.put("mensagem", "Você não tem permissão para excluir um departamento.");
        } 
        else if (uri.contains("/input/admin/departments/edit")) {
            response.put("mensagem", "Você não tem permissão para editar um departamento.");
        }
        else if (uri.contains("/input/admin/departments/saveDepartment")) {
            response.put("mensagem", "Você não tem permissão para cadastrar um departamento.");
        }
        else {
            response.put("mensagem", "Você não tem permissão para acessar este recurso.");
        }
        if (isAjax || method.equals("POST") || method.equals("DELETE") || 
            (method.equals("GET") && uri.contains("/input/admin/removeDepartment"))) {
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

        if (request.getRequestURI().contains("/input/admin/departments/save")) {
            response.put("mensagem", "Erro durante o cadastro: " + ex.getMessage());
        } 
        else if (request.getRequestURI().contains("/input/admin/removeDepartment")) {
            response.put("mensagem", "Erro durante a remoção: " + ex.getMessage());
        }
        else if (request.getRequestURI().contains("/input/admin/departments/edit")) {
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

        if (request.getRequestURI().contains("/input/admin/removeDepartment")) {
            response.put("mensagem", "Não é possível remover o departamento, pois ele está vinculado a um ou mais usuários.");
        } else {
            response.put("mensagem", "Operação violou regras de integridade do banco de dados.");
        }

        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // HTTP 409 - Conflito
    }

} 