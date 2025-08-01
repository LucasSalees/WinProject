package com.project.system.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.system.dto.StandardResponseDTO;
import com.project.system.entity.User;
import com.project.system.service.CommomUserService;
import com.project.system.utils.AuthenticationUtils;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@PreAuthorize("isAuthenticated()")
public class CommonUserController {

    @Autowired
    private CommomUserService CommomUserService;

    @DeleteMapping("/removePhotoCadastro")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> removePhotoCadastro(
            @RequestParam(value = "removePhoto", required = false) Boolean removePhoto,
            Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return CommomUserService.removePhotoCadastro(removePhoto, loggedUser);
    }

    @DeleteMapping("/removePhoto")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> removePhoto(
            @RequestParam String fileName, 
            @RequestParam Long userId,
            Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return CommomUserService.removePhoto(fileName, userId, loggedUser);
    }

    @PostMapping("/verify-email")
    @ResponseBody
    public Map<String, Boolean> verifyEmail(@RequestParam("email") String email,
                                            @RequestParam(value = "id", required = false) Long id) {
        return CommomUserService.verifyEmail(email, id);
    }

    @PostMapping("/changeMyPassword")
    @ResponseBody
    public ResponseEntity<StandardResponseDTO> changeMyPassword(@RequestParam String senha,
                                                                @RequestParam String confirmarSenha,
                                                                Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return CommomUserService.changeMyPassword(senha, confirmarSenha, loggedUser);
    }

    @GetMapping("/check-first-access")
    public ResponseEntity<Boolean> checkFirstAccess(Authentication authentication) {
        User loggedUser = AuthenticationUtils.getLoggedUser(authentication);
        return ResponseEntity.ok(CommomUserService.checkFirstAccess(loggedUser));
    }
}
