package com.hs.eventio.user;

import com.hs.eventio.auth.AuthDTO;
import com.hs.eventio.common.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;


@Tag(name = "User", description = "User management API")
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

    @Operation(summary = "Find user by Id")
    @GetMapping("/{userId}")
    AuthDTO.RegisterUserResponse findUserById(@PathVariable("userId") UUID userId){
        return userService.findById(userId);
    }

    @Operation(summary = "Upload photo", description = "Upload user profile photo.")
    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    AuthDTO.RegisterUserResponse uploadImage(@RequestAttribute("userId") UUID userId,
                                             @RequestParam("photo") MultipartFile multipartFile) {
        var fileName =fileUploadService.uploadFile(multipartFile);
        return userService.updateUserPhoto(userId, fileName, multipartFile.getContentType(),
                "/api/v1/users/photo/"+fileName);
    }

    @Operation(summary = "Download user photo")
    @GetMapping(value = "/photo/{imageName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
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
