package com.hs.eventio.user;

import com.hs.eventio.common.GlobalDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserService {
    GlobalDTO.RegisterUserResponse registerUser(GlobalDTO.RegisterUserRequest registerUserRequest);

    GlobalDTO.FindUserResponse findUserByUsername(String username);

    GlobalDTO.FindUserResponse findUserByResetToken(String token);

    void updatePassword(GlobalDTO.UpdatePasswordCommand updatePasswordCommand);

    GlobalDTO.FindUserResponse findUserById(UUID userId);

    void createPasswordResetToken(GlobalDTO.CreatePasswordResetTokenCommand build);

    GlobalDTO.RegisterUserResponse updateUserPhoto(UUID userId, MultipartFile multipartFile);

    GlobalDTO.RegisterUserResponse findById(UUID userId);

    void updateUserDetails(UUID userId, GlobalDTO.UpdateUserRequest updateUserRequest);
}
