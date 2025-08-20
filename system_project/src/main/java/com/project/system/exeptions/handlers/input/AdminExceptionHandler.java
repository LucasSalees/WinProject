package com.project.system.exeptions.handlers.input;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.project.system.controller.input.AdminController;
import com.project.system.dto.StandardResponseDTO;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(assignableTypes = AdminController.class)
public class AdminExceptionHandler {

	@ExceptionHandler(AccessDeniedException.class)
	public Object handleAccessDeniedException(HttpServletRequest request, AccessDeniedException ex) {
	    String uri = request.getRequestURI();
	    boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	    String method = request.getMethod();

	    Map<String, String> response = new HashMap<>();
	    response.put("status", "error");

	    if (uri.contains("/input/admin/removeUser")) {
	        response.put("mensagem", "Você não tem permissão para excluir usuários.");
	    } 
	    else if (uri.contains("/input/admin/users/edit")) {
	        response.put("mensagem", "Você não tem permissão para editar usuários.");
	    } 
	    else if (uri.contains("/input/admin/users/save")) {
	        response.put("mensagem", "Você não tem permissão para cadastrar usuários.");
	    } 
	    else {
	        response.put("mensagem", "Ação não permitida para seu perfil.");
	    }
	    if (isAjax || method.equals("POST") || method.equals("DELETE")) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	    }

	    ModelAndView mav = new ModelAndView("accessDenied");
	    mav.addObject("mensagemErro", response.get("mensagem"));
	    return mav;
	}

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "erro");

        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("constraint [userEmail]") || message.contains("duplicate key")) {
                response.put("mensagem", "Este e-mail já está cadastrado no sistema.");
            } 
            else if (message.contains("foreign key constraint")) {
                response.put("mensagem", "Operação não permitida: existem registros vinculados.");
            }
            else {
                response.put("mensagem", "Violação de integridade de dados. Verifique os dados informados.");
            }
        } else {
            response.put("mensagem", "Erro de integridade de dados.");
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("mensagem", "Erro de validação nos campos");
        response.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("mensagem", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(HttpServletRequest request, Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");

        if (request.getRequestURI().contains("/input/admin/users/save")) {
            response.put("mensagem", "Erro durante o cadastro: " + ex.getMessage());
        } else {
            response.put("mensagem", "Ocorreu um erro inesperado no servidor.");
        }

        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<StandardResponseDTO> handleSecurityException(SecurityException ex) {
        // Retorna um erro 403 Forbidden com a mensagem da exceção
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(StandardResponseDTO.error(ex.getMessage()));
    }
} 