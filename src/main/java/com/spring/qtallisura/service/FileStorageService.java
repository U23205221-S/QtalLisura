package com.spring.qtallisura.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir.users:uploads/users}")
    private String userUploadDir;

    @Value("${file.upload-dir.products:uploads/products}")
    private String productUploadDir;

    /**
     * Guarda una imagen de usuario y retorna el nombre del archivo
     */
    public String saveUserImage(MultipartFile file) {
        return saveFile(file, userUploadDir, "user");
    }

    /**
     * Guarda una imagen de producto y retorna el nombre del archivo
     */
    public String saveProductImage(MultipartFile file) {
        return saveFile(file, productUploadDir, "product");
    }

    /**
     * Método genérico para guardar archivos
     */
    private String saveFile(MultipartFile file, String uploadDir, String prefix) {
        if (file == null || file.isEmpty()) {
            log.warn("FileStorageService.saveFile() - Archivo vacío o nulo");
            return null;
        }

        try {
            // Crear directorio si no existe
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Directorio creado: {}", uploadPath);
            }

            // Obtener extensión del archivo
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Generar nombre único para el archivo
            String fileName = prefix + "_" + UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(fileName);

            // Copiar archivo al destino
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("✓ Archivo guardado: {} en {}", fileName, uploadDir);
            return fileName;

        } catch (IOException e) {
            log.error("Error al guardar archivo: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo guardar el archivo: " + e.getMessage());
        }
    }

    /**
     * Elimina una imagen de usuario
     */
    public void deleteUserImage(String fileName) {
        deleteFile(fileName, userUploadDir);
    }

    /**
     * Elimina una imagen de producto
     */
    public void deleteProductImage(String fileName) {
        deleteFile(fileName, productUploadDir);
    }

    /**
     * Método genérico para eliminar archivos
     */
    private void deleteFile(String fileName, String uploadDir) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("✓ Archivo eliminado: {}", fileName);
            }
        } catch (IOException e) {
            log.error("Error al eliminar archivo {}: {}", fileName, e.getMessage());
        }
    }

    /**
     * Valida que el archivo sea una imagen
     */
    public boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp")
        );
    }

    /**
     * Valida el tamaño del archivo (máximo 5MB)
     */
    public boolean isValidSize(MultipartFile file) {
        return file != null && file.getSize() <= 5 * 1024 * 1024; // 5MB
    }
}

