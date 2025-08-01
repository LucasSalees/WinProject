package com.project.system.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.mindrot.jbcrypt.BCrypt;

import com.project.system.utils.PasswordUtils;

public class PasswordUtils {

    public static boolean checkPassword(String plainPassword, String storedPassword) {
        LocalDateTime dataHoraAtual = LocalDateTime.now();
        System.out.println("\n========== VERIFICAÇÃO DE SENHA ==========");
        System.out.println("Data/Hora: " + dataHoraAtual.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        try {
            System.out.println("Senha fornecida (plain): " + plainPassword);
            System.out.println("Senha armazenada: " + storedPassword);

            if (storedPassword == null) {
                System.out.println("Status: Senha armazenada é null! Verificação falhou.");
                System.out.println("=========================================\n");
                return false;
            }

            boolean match;

            if (storedPassword.startsWith("$2a$")) {
                match = BCrypt.checkpw(plainPassword, storedPassword);
                System.out.println("Tipo de senha: Criptografada (BCrypt)");
            } else {
                match = plainPassword.equals(storedPassword);
                System.out.println("Tipo de senha: Texto plano (não criptografada)");
            }

            System.out.println("Resultado da verificação: " + (match ? "CORRETA" : "INCORRETA"));
            System.out.println("=========================================\n");

            return match;

        } catch (Exception e) {
            System.err.println("Erro durante a verificação de senha: " + e.getMessage());
            e.printStackTrace();
            System.out.println("=========================================\n");
            return false;
        }
    }

    public static String hashPassword(String senha) {
        LocalDateTime dataHoraAtual = LocalDateTime.now();
        System.out.println("\n========== HASH DE SENHA ==========");
        System.out.println("Data/Hora: " + dataHoraAtual.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.println("Senha original: omitida por segurança");

        String hashed = BCrypt.hashpw(senha, BCrypt.gensalt());
        System.out.println("Senha criptografada (BCrypt): " + hashed);
        System.out.println("===================================\n");

        return hashed;
    }
}