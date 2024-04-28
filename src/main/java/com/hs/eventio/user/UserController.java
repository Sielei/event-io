package com.hs.eventio.user;

import com.hs.eventio.auth.AuthDTO;
import com.hs.eventio.common.service.FileUploadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
class UserController {
    private final UserService userService;
    private final FileUploadService fileUploadService;
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    UserController(UserService userService, FileUploadService fileUploadService) {
        this.userService = userService;
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("/{userId}")
    AuthDTO.RegisterUserResponse findUserById(@PathVariable("userId") UUID userId){
        return userService.findById(userId);
    }

    @PostMapping("/upload-image")
    AuthDTO.RegisterUserResponse uploadImage(@RequestAttribute("userId") UUID userId,
                                             @RequestParam("photo") MultipartFile multipartFile) {
        var fileName =fileUploadService.uploadFile(multipartFile);
        return userService.updateUserPhoto(userId, fileName, multipartFile.getContentType(),
                "/api/v1/users/download-file/"+fileName);
    }

    @GetMapping("/download-image/{imageName}")
    public ResponseEntity<Resource> downloadImage(@PathVariable("imageName") String fileName, HttpServletRequest request) throws MalformedURLException {
        var resource = fileUploadService.loadFileAsResource(fileName);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
