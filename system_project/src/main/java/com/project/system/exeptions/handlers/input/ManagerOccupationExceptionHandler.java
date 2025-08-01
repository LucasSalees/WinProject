package com.project.system.exeptions.handlers.input;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.project.system.controller.input.ManagerOccupationController;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(assignableTypes = ManagerOccupationController.class)
public class ManagerOccupationExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(HttpServletRequest request, AccessDeniedException ex) {
        String uri = request.getRequestURI();
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        String method = request.getMethod();

        Map<String, String> response = new HashMap<>();
        response.put("status", "error");

        if (uri.contains("/input/manager/removeOccupation")) {
        	response.put("mensagem", "Você não tem permissão para excluir uma profissão.");
        } 
        else if (uri.contains("/input/manager/occupations/edit")) {
            response.put("mensagem", "Você não tem permissão para editar uma profissão.");
        }
        else if  (uri.contains("/input/manager/occupations/save")) {
            response.put("mensagem", "Você não tem permissão para editar uma profissão.");
        }
        else {
            response.put("mensagem", "Você não tem permissão para esta ação.");
        }
        if (isAjax || method.equals("POST") || method.equals("DELETE") || 
            (method.equals("GET") && uri.contains("/input/director/removeOccupation"))) {
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

        if (request.getRequestURI().contains("/input/manager/occupations/save")) {
            response.put("mensagem", "Erro durante o cadastro: " + ex.getMessage());
        } 
        else if (request.getRequestURI().contains("/input/manager/removeOccupation")) {
            response.put("mensagem", "Erro durante a remoção: " + ex.getMessage());
        }
        else if (request.getRequestURI().contains("/input/manager/occupations/edit")) {
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

        if (request.getRequestURI().contains("/input/manager/removeOccupation")) {
            response.put("mensagem", "Não é possível remover a profissão, pois ela está vinculada a um ou mais usuários.");
        } else {
            response.put("mensagem", "Operação violou restrições de integridade do banco de dados.");
        }

        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 - conflito
    }

}