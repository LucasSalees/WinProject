package com.project.system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) throws IOException {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadPath);
    }

    public String storeFile(MultipartFile file) throws IOException {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(originalFilename).toLowerCase();

        // Verificações de segurança
        if (originalFilename.contains("..")) {
            throw new IOException("Nome do arquivo inválido: " + originalFilename);
        }

        if (!file.getContentType().startsWith("image/") ||
            !(extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg") ||
              extension.equals("gif") || extension.equals("webp"))) {
            throw new IOException("Tipo de arquivo inválido. Somente imagens são permitidas.");
        }

        // Gera nome único
        String filename = UUID.randomUUID() + "." + extension;
        Path targetPath = this.uploadPath.resolve(filename);

        // Salva o arquivo
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    public void deleteFile(String filename) throws IOException {
        if (filename == null || filename.isBlank()) return;

        Path filePath = this.uploadPath.resolve(filename).normalize();
        if (Files.exists(filePath) && filePath.startsWith(this.uploadPath)) {
            Files.delete(filePath);
        }
    }
    
    public boolean exists(String filename) {
        if (filename == null || filename.isBlank()) return false;

        Path filePath = this.uploadPath.resolve(filename).normalize();
        // Garante que o caminho está dentro da pasta upload e que o arquivo existe
        return Files.exists(filePath) && filePath.startsWith(this.uploadPath);
    }

}