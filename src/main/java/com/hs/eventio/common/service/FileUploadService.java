package com.hs.eventio.common.service;

import com.hs.eventio.common.exception.FileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileUploadService {
    private Path fileUploadLocation;
    private static final Logger log = LoggerFactory.getLogger(FileUploadService.class);

    public void createFileUploadDirectory(String path) {
        this.fileUploadLocation = Paths.get(path).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileUploadLocation);
        }
        catch (Exception exception){
            log.error("Failed to create upload directory!",exception);
        }
    }

    public String uploadFile(MultipartFile multipartFile, String path) {
        createFileUploadDirectory(path);
        var fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try {
            var targetLocation = this.fileUploadLocation.resolve(fileName);
            Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException exception){
            log.error("Could not upload file!", exception);
        }
        return fileName;
    }

    public Resource loadFileAsResource(String fileName, String path) throws MalformedURLException {
        createFileUploadDirectory(path);
        var filePath = this.fileUploadLocation.resolve(fileName).normalize();
        var resource = new UrlResource(filePath.toUri());
        if (resource.exists()){
            return resource;
        }
        else {
            throw new FileException("File not found: " + fileName);
        }
    }
}
