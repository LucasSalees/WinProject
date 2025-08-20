package com.project.system.utils;

import com.project.system.entity.User;
import com.project.system.enums.input.UserRole;
import com.project.system.enums.input.UserPermission;
import java.util.Set;

public class RoleValidator {

    public static void validateRoleChange(User loggedUser, UserRole targetRole, User targetUser, Set<UserPermission> targetUserPermissions) {
        UserRole loggedRole = loggedUser.getUserRole();
        
        if (loggedUser.getUserId().equals(targetUser.getUserId())) {
            if (targetRole.getLevel() > loggedRole.getLevel()) {
                throw new SecurityException("Você não pode elevar seu próprio nível de acesso.");
            }
            if (targetRole.getLevel() < loggedRole.getLevel()) {
                throw new SecurityException("Você não pode reduzir seu próprio nível de acesso.");
            }
            return;
        }
        

        // 1. Regras para o Diretor
        if (loggedRole == UserRole.DIRECTOR) {
            // O Diretor pode alterar qualquer outro usuário, sem restrições.
            return;
        }

        // 2. Regras para o Admin
        if (loggedRole == UserRole.ADMIN) {
            if (targetUser.getUserRole().getLevel() < loggedRole.getLevel()) {
            	
                if (targetRole.getLevel() > loggedRole.getLevel()) {
                    throw new SecurityException("Você não pode atribuir um nível de acesso acima do seu.");
                }
            } else if (targetUser.getUserRole().getLevel() == loggedRole.getLevel()) {

                if (targetRole.getLevel() > loggedRole.getLevel()) {
                    throw new SecurityException("Você não pode elevar o nível de acesso de outro administrador.");
                }
            } else {
                throw new SecurityException("Você não pode alterar um usuário com nível superior ao seu.");
            }
        }
        
        // 3. Regras para o Gerente
        if (loggedRole == UserRole.MANAGER) {
            if (targetUser.getUserRole().getLevel() < loggedRole.getLevel()) {

                if (targetRole.getLevel() > loggedRole.getLevel()) {
                    throw new SecurityException("Você não pode atribuir um nível de acesso acima do seu.");
                }
            } else if (targetUser.getUserRole().getLevel() == loggedRole.getLevel()) {

                if (targetRole.getLevel() > loggedRole.getLevel()) {
                    throw new SecurityException("Você não pode elevar o nível de acesso de outro gerente.");
                }
            } else {
                throw new SecurityException("Você não pode alterar um usuário com nível superior ao seu.");
            }
        }

        // 4. Regras para o Usuário
        if (loggedRole == UserRole.USER) {
            if (targetUser.getUserRole().getLevel() < loggedRole.getLevel()) {

                if (targetRole.getLevel() > loggedRole.getLevel()) {
                    throw new SecurityException("Você não pode atribuir um nível de acesso acima do seu.");
                }
            } else if (targetUser.getUserRole().getLevel() == loggedRole.getLevel()) {

                if (targetRole.getLevel() > loggedRole.getLevel()) {
                    throw new SecurityException("Você não pode elevar o nível de acesso de outro usuário.");
                }
            } else {
                throw new SecurityException("Você não pode alterar de um usuário com nível superior ao seu.");
            }
        }

        Set<UserPermission> loggedUserAllowedPermissions = loggedUser.getUserRole().getBasePermissions();
        for (UserPermission permission : targetUserPermissions) {
            if (!loggedUserAllowedPermissions.contains(permission)) {
                throw new SecurityException("Você não pode atribuir a permissão '" + permission.getLabel() + "' a este usuário.");
            }
        }
    }
}