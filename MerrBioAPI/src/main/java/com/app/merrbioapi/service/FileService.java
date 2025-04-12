package com.app.merrbioapi.service;

import com.app.merrbioapi.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${app.upload.dir:${user.dir}/src/main/resources/static/img}")
    private String uploadDir;

    @Value("${app.upload.base-url:/img}")
    private String baseUrl;

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Get original filename and extension
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);

        // Generate unique filename
        String uniqueFilename = generateUniqueFilename(fileExtension);

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save the file
            Path destinationFile = uploadPath.resolve(uniqueFilename);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Return the relative URL
            return baseUrl + "/" + uniqueFilename;

        } catch (IOException e) {
            throw new FileStorageException("Failed to store file " + originalFilename, e);
        }
    }


    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        // Extract filename from the URL path
        String filename = filePath.substring(filePath.lastIndexOf('/') + 1);

        try {
            Path file = Paths.get(uploadDir).resolve(filename);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            return false;
        }
    }


    private String generateUniqueFilename(String extension) {
        return UUID.randomUUID().toString() + extension;
    }


    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return ""; // No extension
        }
        return filename.substring(dotIndex);
    }
}