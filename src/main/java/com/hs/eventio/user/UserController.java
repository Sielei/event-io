package com.hs.eventio.user;

import com.hs.eventio.common.GlobalDTO;
import com.hs.eventio.common.config.EventioApplicationConfigData;
import com.hs.eventio.common.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private final EventioApplicationConfigData eventioApplicationConfigData;
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    UserController(UserService userService, FileUploadService fileUploadService, EventioApplicationConfigData eventioApplicationConfigData) {
        this.userService = userService;
        this.fileUploadService = fileUploadService;
        this.eventioApplicationConfigData = eventioApplicationConfigData;
    }

    @Operation(summary = "Find user by Id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{userId}")
    GlobalDTO.RegisterUserResponse findUserById(@PathVariable("userId") UUID userId){
        return userService.findById(userId);
    }

    @Operation(summary = "Update user details", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateUser(@PathVariable("userId") UUID userId, @Valid @RequestBody GlobalDTO.UpdateUserRequest updateUserRequest){
        userService.updateUserDetails(userId, updateUserRequest);
    }

    @Operation(summary = "Upload photo", description = "Upload user profile photo.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    GlobalDTO.RegisterUserResponse uploadImage(@RequestAttribute("userId") UUID userId,
                                               @RequestParam("photo") MultipartFile multipartFile) {
        return userService.updateUserPhoto(userId, multipartFile);
    }

    @Operation(summary = "Download user photo", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/photo/{imageName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadImage(@PathVariable("imageName") String fileName, HttpServletRequest request) throws MalformedURLException {
        var resource = fileUploadService.loadFileAsResource(fileName, eventioApplicationConfigData.getUserPhotosUploadLocation());
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
