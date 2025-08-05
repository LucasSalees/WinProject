package com.project.system.security;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Service;

import com.project.system.entity.User;
import com.project.system.exceptions.DayAccessRestrictedException;
import com.project.system.exceptions.TimeAccessRestrictedException;
import com.project.system.exceptions.UserBlockedException;
import com.project.system.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            // Busca usuário por e-mail
        	User user = userRepository.findByEmailWithPermissions(email.toLowerCase().trim())
        		    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        		// Debug: imprima as permissões carregadas
        	// System.out.println("Usuário: " + user.getUserEmail());
        	// System.out.println("Permissões do usuário:");
        	// if(user.getPermissions() != null) {
        	//   user.getPermissions().forEach(p -> System.out.println(" - " + p.name()));
        	// } else {
    		//   System.out.println("Nenhuma permissão carregada.");
        	// }

            // ✅ ATENÇÃO:
            // A senha será verificada automaticamente pelo Spring Security depois desse método retornar.
            // Portanto, não verifique a senha manualmente aqui.

            // Validações personalizadas:
            LocalTime now = LocalTime.now();
            String nowDay = convertToPortuguese(LocalDate.now().getDayOfWeek());

            if (Boolean.TRUE.equals(user.isUserInactive())) {
                throw new UserBlockedException("Usuário inativo permanentemente. Contate o administrador.");
            }

            if (Boolean.TRUE.equals(user.isUserSuspended())) {
                throw new UserBlockedException("Usuário suspenso permanentemente. Contate o administrador.");
            }

            if (Boolean.TRUE.equals(user.isUserLocked())) {
                throw new UserBlockedException("Usuário bloqueado permanentemente. Contate o administrador.");
            }

            if (!Boolean.TRUE.equals(user.getUserActive())) {
                throw new UserBlockedException("Usuário ainda não ativado. Contate o administrador.");
            }

            if (user.getAllowedDays() == null || user.getAllowedDays().isEmpty()) {
                throw new DayAccessRestrictedException("Acesso restrito: nenhum dia permitido para o usuário.");
            }

            if (!user.getAllowedDays().contains(nowDay)) {
                throw new DayAccessRestrictedException("Acesso restrito: não permitido no dia " + nowDay);
            }

            if (user.getStartTime() != null && now.isBefore(user.getStartTime())) {
                throw new TimeAccessRestrictedException("Acesso restrito: permitido apenas após " + user.getStartTime());
            }

            if (user.getEndTime() != null && now.isAfter(user.getEndTime())) {
                throw new TimeAccessRestrictedException("Acesso restrito: permitido apenas antes de " + user.getEndTime());
            }

            // Tudo certo: retorna o usuário com as informações que Spring precisa
            return new UserDetailsImpl(user);

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
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
}