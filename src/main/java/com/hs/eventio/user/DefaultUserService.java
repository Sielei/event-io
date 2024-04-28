package com.hs.eventio.user;

import com.hs.eventio.auth.AuthDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
class DefaultUserService implements UserService {
    private final UserRepository userRepository;

    DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public AuthDTO.RegisterUserResponse registerUser(AuthDTO.RegisterUserRequest registerUserRequest) {
        verifyEmailIsUnique(registerUserRequest.email());
        var newUser = userRepository.save(mapRegisterUserRequestToUser(registerUserRequest));
        return mapUserToRegisterUserResponse(newUser);
    }

    private AuthDTO.RegisterUserResponse mapUserToRegisterUserResponse(User user) {
        return new AuthDTO.RegisterUserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getPhoto().getImageUrl());
    }

    private User mapRegisterUserRequestToUser(AuthDTO.RegisterUserRequest registerUserRequest) {

        return User.builder()
                .name(registerUserRequest.name())
                .email(registerUserRequest.email())
                .password(registerUserRequest.password())
                .photo(Photo.builder()
                        .imageName("default.png")
                        .imageType("image/png")
                        .imageUrl("/api/v1/users/download-file/default.png")
                        .build())
                .build();
    }

    private void verifyEmailIsUnique(String email) {
        var userWithSimilarEmail = userRepository.findUserByEmail(email);
        if (userWithSimilarEmail.isPresent()){
            throw new RuntimeException("User with similar email exists!");
        }
    }

    @Override
    public AuthDTO.FindUserResponse findUserByUsername(String username) {
        return userRepository.findUserByEmail(username)
                .map(user -> new AuthDTO.FindUserResponse(user.getId(), user.getName(),
                        user.getEmail(), user.getPassword(), List.of("USER"),
                        user.getPhoto().getImageUrl()))
                .orElseThrow();
    }

    @Override
    public AuthDTO.FindUserResponse findUserByEmail(String email) {
        return null;
    }

    @Override
    public AuthDTO.FindUserResponse findUserByResetToken(String token) {
        return null;
    }

    @Override
    public void updatePassword(AuthDTO.UpdatePasswordCommand updatePasswordCommand) {

    }

    @Override
    public AuthDTO.FindUserResponse findUserById(UUID userId) {
        return null;
    }

    @Override
    public void createPasswordResetToken(AuthDTO.CreatePasswordResetTokenCommand build) {
    }

    @Override
    public AuthDTO.RegisterUserResponse updateUserPhoto(UUID userId, String fileName, String contentType, String s) {
        return null;
    }

    @Override
    public AuthDTO.RegisterUserResponse findById(UUID userId) {
        var user = userRepository.findUserById(userId);
        return new AuthDTO.RegisterUserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getPhoto().getImageUrl());
    }
}
