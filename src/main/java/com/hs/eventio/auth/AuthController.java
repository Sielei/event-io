package com.hs.eventio.auth;

import com.hs.eventio.common.GlobalDTO;
import com.hs.eventio.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@Tag(name = "Authentication", description = "Authentication API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    AuthController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Register as a User", description = "Register to use the application")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GlobalDTO.RegisterUserResponse> registerUser(
            @Valid @RequestBody GlobalDTO.RegisterUserRequest registerUserRequest){
        var regUser = userService.registerUser(authenticationService.encodeRawPassword(registerUserRequest));
        var location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/users/{id}")
                .buildAndExpand(regUser.id()).toUri();
        return ResponseEntity.created(location).body(regUser);
    }

    @Operation(summary = "Login", description = "Login to use the application")
    @PostMapping("/login")
    GlobalDTO.LoginResponse login(@Valid @RequestBody GlobalDTO.LoginRequest loginRequest,
                                  HttpServletRequest request){
        return authenticationService.login(loginRequest, request);
    }

    @Operation(summary = "Update user Password")
    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateUserPassword(@RequestAttribute("userId") UUID userId,
                            @Valid @RequestBody GlobalDTO.UpdatePasswordRequest updatePasswordRequest){
        authenticationService.updatePassword(userId, updatePasswordRequest);
    }

    @Operation(summary = "Get Password Reset Token", description = "Use a registered email address to get password reset token")
    @PostMapping("/get-reset-token")
    GlobalDTO.GetResetTokenResponse sendResetToken(
            @Valid @RequestBody GlobalDTO.GetResetTokenRequest getResetTokenRequest){
        authenticationService.generatePasswordResetToken(getResetTokenRequest.email());
        return new GlobalDTO.GetResetTokenResponse("Ok",
                "A password reset link has been sent to "
                        + getResetTokenRequest.email());
    }

    @Operation(summary = "Reset Password", description = "Use the reset token received via email to reset password")
    @PostMapping("/reset-password")
    GlobalDTO.GetResetTokenResponse resetPassword(@Valid @RequestBody GlobalDTO.ResetPasswordRequest resetPasswordRequest){
        authenticationService.resetPasswordWithResetToken(resetPasswordRequest);
        return new GlobalDTO.GetResetTokenResponse("Ok",
                "Password reset successfully! Proceed to login page to login with your new password!");
    }
}
