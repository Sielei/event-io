package com.hs.eventio.user;

import com.hs.eventio.auth.AuthDTO;

import java.util.UUID;

public interface UserService {
    AuthDTO.RegisterUserResponse registerUser(AuthDTO.RegisterUserRequest registerUserRequest);

    AuthDTO.FindUserResponse findUserByUsername(String username);

    AuthDTO.FindUserResponse findUserByEmail(String email);

    AuthDTO.FindUserResponse findUserByResetToken(String token);

    void updatePassword(AuthDTO.UpdatePasswordCommand updatePasswordCommand);

    AuthDTO.FindUserResponse findUserById(UUID userId);

    void createPasswordResetToken(AuthDTO.CreatePasswordResetTokenCommand build);

    AuthDTO.RegisterUserResponse updateUserPhoto(UUID userId, String fileName, String contentType, String s);

    AuthDTO.RegisterUserResponse findById(UUID userId);
}
