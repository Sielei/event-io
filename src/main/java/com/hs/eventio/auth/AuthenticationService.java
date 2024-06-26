package com.hs.eventio.auth;

import com.hs.eventio.common.GlobalDTO;
import com.hs.eventio.common.exception.PasswordValidationException;
import com.hs.eventio.common.exception.UserAuthenticationException;
import com.hs.eventio.email.EmailUtil;
import com.hs.eventio.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
class AuthenticationService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailUtil emailUtil;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationService(UserService userService, AuthenticationManager authenticationManager,
                                 BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtil jwtUtil, EmailUtil emailUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailUtil = emailUtil;
    }

    public GlobalDTO.LoginResponse login(GlobalDTO.LoginRequest loginRequest, HttpServletRequest request) {
        var requestIp = request.getRemoteAddr();
        log.info("Login attempted by user: {} from IP address: {}", loginRequest.email(), requestIp);
        var userDto = userService.findUserByUsername(loginRequest.email())
                .orElseThrow(() -> new UserAuthenticationException("Failed to authenticate user! Username or password is incorrect"));
        var authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.email(),
                loginRequest.password());
        var isAuthenticated = false;
        try {
            var authManager = authenticationManager.authenticate(authenticationToken);
            isAuthenticated = authManager.isAuthenticated();
            log.info("{} authenticated successfully", userDto.name());
        }
        catch (AuthenticationException authenticationException){
            log.error("Authentication for {} failed because of {}!", userDto.name(), authenticationException.getMessage());
        }
        if (isAuthenticated){
            var jwtToken = jwtUtil.generateJWTToken(userDto);
            return new GlobalDTO.LoginResponse(jwtToken, userDto.id(), userDto.name(), userDto.email(),
                    userDto.photoUrl());
        }
        else {
            throw new UserAuthenticationException("Failed to authenticate user! Username or password is incorrect");
        }
    }
    public void  generatePasswordResetToken(String email) {
        var user = userService.findUserByUsername(email)
                .orElseThrow(() -> new UserAuthenticationException("Email: " +
                        email + " does not exist"));
        var token = UUID.randomUUID().toString();
        userService.createPasswordResetToken(GlobalDTO.CreatePasswordResetTokenCommand.builder()
                .userId(user.id())
                .token(token)
                .build());
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", user.name());
        properties.put("passwordResetToken", token);
        var subject = "Password Reset";
        var template = "password-reset.html";
        emailUtil.sendEmail(email, subject, template, properties);
    }

    public void resetPasswordWithResetToken(GlobalDTO.ResetPasswordRequest resetPasswordRequest) {
        var user = userService.findUserByResetToken(resetPasswordRequest.token());
        var newPassword = bCryptPasswordEncoder.encode(resetPasswordRequest.newPassword());
        userService.updatePassword(new GlobalDTO.UpdatePasswordCommand(user.id(), newPassword));
    }

    public GlobalDTO.RefreshToken getRefreshToken(UUID userId){
        var userDto = userService.findUserById(userId);
        //create a new session
        var jwtToken = jwtUtil.generateJWTToken(userDto);
        //return new token
        return new GlobalDTO.RefreshToken(jwtToken);
    }

    public void updatePassword(UUID userId, GlobalDTO.UpdatePasswordRequest updatePasswordRequest) {
        var userDto = userService.findUserById(userId);
        if (!bCryptPasswordEncoder.matches(updatePasswordRequest.currentPassword(), userDto.password())){
            throw new UserAuthenticationException("Current password does not match with the password you provided!");
        }
        var newPassword = bCryptPasswordEncoder.encode(updatePasswordRequest.newPassword());
        var updatePasswordCommand =  new GlobalDTO.UpdatePasswordCommand(userDto.id(), newPassword);
        userService.updatePassword(updatePasswordCommand);
    }
    public GlobalDTO.RegisterUserRequest encodeRawPassword(GlobalDTO.RegisterUserRequest registerUserRequest){
        return new GlobalDTO.RegisterUserRequest(registerUserRequest.name(), registerUserRequest.email(),
                bCryptPasswordEncoder.encode(registerUserRequest.password()));
    }
}
