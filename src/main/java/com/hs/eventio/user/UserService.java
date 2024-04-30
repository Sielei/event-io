package com.hs.eventio.user;

import com.hs.eventio.auth.AuthDTO;

import java.util.UUID;

public interface UserService {
    AuthDTO.RegisterUserResponse registerUser(AuthDTO.RegisterUserRequest registerUserRequest);

    AuthDTO.FindUserResponse findUserByUsername(String username);

    AuthDTO.FindUserResponse findUserByResetToken(String token);

    void updatePassword(AuthDTO.UpdatePasswordCommand updatePasswordCommand);

    AuthDTO.FindUserResponse findUserById(UUID userId);

    void createPasswordResetToken(AuthDTO.CreatePasswordResetTokenCommand build);

    AuthDTO.RegisterUserResponse updateUserPhoto(UUID userId, String imageName, String imageType,
                                                 String imageUrl);

    AuthDTO.RegisterUserResponse findById(UUID userId);

    void updateUserDetails(UUID userId, AuthDTO.UpdateUserRequest updateUserRequest);
}
