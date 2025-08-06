package com.project.system.security;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Service;

import com.project.system.entity.User;
import com.project.system.enums.input.AuditAction;
import com.project.system.enums.input.AuditClassName;
import com.project.system.enums.input.UserRole;
import com.project.system.exceptions.DayAccessRestrictedException;
import com.project.system.exceptions.TimeAccessRestrictedException;
import com.project.system.exceptions.UserBlockedException;
import com.project.system.repositories.UserRepository;
import com.project.system.service.AuditService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuditService auditService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	 try {
    	        // Busca usuário por e-mail (com permissões)
    	        User user = userRepository.findByEmailWithPermissions(email.toLowerCase().trim())
    	                .orElseThrow(() -> {
    	                    logFailedLogin(null, email, "Usuário não encontrado.");
    	                    return new UsernameNotFoundException("Usuário não encontrado");
    	                });

    	        // Verificações de bloqueio, suspensão etc.
    	        LocalTime now = LocalTime.now();
    	        String nowDay = convertToPortuguese(LocalDate.now().getDayOfWeek());

            if (Boolean.TRUE.equals(user.isUserInactive())) {
                logFailedLogin(user.getUserId(), user.getUserEmail(), "Usuário inativo permanentemente.");
                throw new UserBlockedException("Usuário inativo permanentemente. Contate o administrador.");
            }

            if (Boolean.TRUE.equals(user.isUserSuspended())) {
                logFailedLogin(user.getUserId(), user.getUserEmail(), "Usuário suspenso permanentemente.");
                throw new UserBlockedException("Usuário suspenso permanentemente. Contate o administrador.");
            }

            if (Boolean.TRUE.equals(user.isUserLocked())) {
                logFailedLogin(user.getUserId(),  user.getUserEmail(), "Usuário bloqueado permanentemente.");
                throw new UserBlockedException("Usuário bloqueado permanentemente. Contate o administrador.");
            }

            if (!Boolean.TRUE.equals(user.getUserActive())) {
                logFailedLogin(user.getUserId(), user.getUserEmail(), "Usuário não ativado.");
                throw new UserBlockedException("Usuário ainda não ativado. Contate o administrador.");
            }

            if (user.getAllowedDays() == null || user.getAllowedDays().isEmpty()) {
                logFailedLogin(user.getUserId(), user.getUserEmail(), "Usuário com nenhum dia de acesso permitido.");
                throw new DayAccessRestrictedException("Acesso restrito: nenhum dia permitido para o usuário.");
            }

            if (!user.getAllowedDays().contains(nowDay)) {
                logFailedLogin(user.getUserId(), user.getUserEmail(), "Usuário não tem permissão para acessar na " + nowDay + ".");
                throw new DayAccessRestrictedException("Acesso restrito: não permitido no dia " + nowDay);
            }

            if (user.getStartTime() != null && now.isBefore(user.getStartTime())) {
                logFailedLogin(user.getUserId(), user.getUserEmail(), "Usuário tentou acessar antes do horário permitido.");
                throw new TimeAccessRestrictedException("Acesso restrito: permitido apenas após " + user.getStartTime());
            }

            if (user.getEndTime() != null && now.isAfter(user.getEndTime())) {
                logFailedLogin(user.getUserId(), user.getUserEmail(), "Usuário tentou acessar após o horário permitido.");
                throw new TimeAccessRestrictedException("Acesso restrito: permitido apenas antes de " + user.getEndTime());
            }

            // Tudo certo: retorna o usuário com as informações que Spring precisa
            return new UserDetailsImpl(user);

        } catch (UsernameNotFoundException e) {
            logFailedLogin(null, email, "Usuário não encontrado.");
            throw e;

        } catch (UserBlockedException | DayAccessRestrictedException | TimeAccessRestrictedException e) {
            // ✅ Exceções já logadas — apenas propaga
            throw e;

        } catch (Exception e) {
            // ✅ Exceções inesperadas — loga aqui
            logFailedLogin(null, email, "Erro interno ao tentar autenticar: " + e.getMessage());
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }

    private String convertToPortuguese(DayOfWeek day) {
        switch (day) {
            case MONDAY: return "Segunda";
            case TUESDAY: return "Terça";
            case WEDNESDAY: return "Quarta";
            case THURSDAY: return "Quinta";
            case FRIDAY: return "Sexta";
            case SATURDAY: return "Sábado";
            case SUNDAY: return "Domingo";
            default: return "";
        }
    }
    
    private void logFailedLogin(Long userId, String userEmail, String description) {
        auditService.logAudit(
            AuditAction.LOGIN_FAILED,
            AuditClassName.AUTHENTICATION.name(),
            userId != null ? String.valueOf(userId) : null,
            description,
            null,
            null,
            userId != null ? UserRole.USER.name() : UserRole.USER.name(),
            userId != null ? String.valueOf(userId) : null,
            userEmail
        );
    }
}