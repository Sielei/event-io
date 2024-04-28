package com.hs.eventio.auth;

import com.hs.eventio.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    AuthController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping
    ResponseEntity<AuthDTO.RegisterUserResponse> registerUser(
            @Valid @RequestBody AuthDTO.RegisterUserRequest registerUserRequest){
        var regUser = userService.registerUser(authenticationService.encodeRawPassword(registerUserRequest));
        var location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/users/{id}")
                .buildAndExpand(regUser.id()).toUri();
        return ResponseEntity.created(location).body(regUser);
    }

    @PostMapping("/login")
    AuthDTO.LoginResponse login(@Valid @RequestBody AuthDTO.LoginRequest loginRequest,
                                HttpServletRequest request){
        return authenticationService.login(loginRequest, request);
    }

    @PostMapping("/get-reset-token")
    AuthDTO.GetResetTokenResponse sendResetToken(
            @Valid @RequestBody AuthDTO.GetResetTokenRequest getResetTokenRequest){
        authenticationService.generatePasswordResetToken(getResetTokenRequest.email());
        return new AuthDTO.GetResetTokenResponse("Ok",
                "A password reset link has been sent to "
                        + getResetTokenRequest.email());
    }

    @PostMapping("/reset-password")
    AuthDTO.GetResetTokenResponse resetPassword(@Valid @RequestBody AuthDTO.ResetPasswordRequest resetPasswordRequest){
        authenticationService.resetPasswordWithResetToken(resetPasswordRequest);
        return new AuthDTO.GetResetTokenResponse("Ok",
                "Password reset successfully! Proceed to login page to login with your new password!");
    }
}
