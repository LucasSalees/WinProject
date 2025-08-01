package com.project.system.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.project.system.entity.User;
import com.project.system.repositories.UserRepository;
import com.project.system.utils.PasswordUtils;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
public class PasswordMigrationConfig {

    private final UserRepository userRepository;

    @Autowired
    public PasswordMigrationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public CommandLineRunner migratePasswords() {
        return args -> {
            System.out.println("========== INÍCIO DA MIGRAÇÃO DE SENHAS ==========");
            System.out.println("Data/Hora: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

            try {
                // Migração de senhas de usuários
                List<User> users = userRepository.findAll();
                int migrateUsers = 0;
                for (User user : users) {
                    if (!user.getUserPassword().startsWith("$2a$")) {
                        user.setUserPassword(PasswordUtils.hashPassword(user.getUserPassword()));
                        userRepository.save(user);
                        migrateUsers++;
                    }
                }

                // Resultado final
                System.out.println("Usuários com senha migrada: " + migrateUsers + " de " + users.size());
                System.out.println("========== MIGRAÇÃO CONCLUÍDA ==========");

            } catch (Exception e) {
                System.err.println("Erro ao migrar senhas: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
