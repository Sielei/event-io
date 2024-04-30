package com.hs.eventio.user;

import com.hs.eventio.auth.AuthDTO;
import com.hs.eventio.common.exception.ResourceNotFoundException;
import com.hs.eventio.common.exception.UserRegistrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultUserService.class);

    DefaultUserService(UserRepository userRepository, PasswordResetRepository passwordResetRepository) {
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
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
                        .imageUrl("/api/v1/users/photo/default.png")
                        .build())
                .build();
    }

    private void verifyEmailIsUnique(String email) {
        var userWithSimilarEmail = userRepository.findUserByEmail(email);
        if (userWithSimilarEmail.isPresent()){
            throw new UserRegistrationException("User with similar email exists!");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public AuthDTO.FindUserResponse findUserByUsername(String username) {
        return userRepository.findUserByEmail(username)
                .map(user -> {
                    var roleDto = mapRolesToRoleDtos(user.getRoles());
                    return new AuthDTO.FindUserResponse(user.getId(), user.getName(),
                            user.getEmail(), user.getPassword(), roleDto,
                            user.getPhoto().getImageUrl());
                })
                .orElseThrow(() -> new ResourceNotFoundException("User with email: " +
                        username + " does not exist!"));
    }

    @Override
    public AuthDTO.FindUserResponse findUserByResetToken(String token) {
        var passwordResetToken = passwordResetRepository.findByToken(token)
                .orElseThrow();
        var user = passwordResetToken.getUser();
        return mapUserToFindUserResponse(user);
    }

    private AuthDTO.FindUserResponse mapUserToFindUserResponse(User user) {
        return new AuthDTO.FindUserResponse(user.getId(), user.getName(), user.getEmail(), user.getPassword(),
                mapRolesToRoleDtos(user.getRoles()), user.getPhoto().getImageUrl());
    }
     private Set<AuthDTO.RoleDto> mapRolesToRoleDtos(Set<Role> roles){
        return roles.stream()
                .map(role -> new AuthDTO.RoleDto(role.getId(), role.getName(),
                        role.getDescription(), role.getAuthority()))
                .collect(Collectors.toSet());
     }

    @Transactional
    @Override
    public void updatePassword(AuthDTO.UpdatePasswordCommand updatePasswordCommand) {
        var user = userRepository.findUserById(updatePasswordCommand.userId());
        user.setPassword(updatePasswordCommand.newPassword());
        userRepository.save(user);
    }

    @Override
    public AuthDTO.FindUserResponse findUserById(UUID userId) {
        return null;
    }

    @Transactional
    @Override
    public void createPasswordResetToken(AuthDTO.CreatePasswordResetTokenCommand passwordResetTokenCommand) {
        var exp = LocalDate.now().plusDays(1);
        var expiry = Date.from(exp.atStartOfDay(ZoneId.systemDefault()).toInstant());
        var user = userRepository.findUserById(passwordResetTokenCommand.userId());
        var passwordReset = PasswordReset.builder()
                .token(passwordResetTokenCommand.token())
                .user(user)
                .expiry(expiry)
                .used(false)
                .build();
        passwordResetRepository.save(passwordReset);
    }

    @Transactional
    @Override
    public AuthDTO.RegisterUserResponse updateUserPhoto(UUID userId, String imageName, String imageType,
                                                        String imageUrl) {
        var user = userRepository.findUserById(userId);
        user.setPhoto(Photo.builder()
                        .imageName(imageName)
                        .imageType(imageType)
                        .imageUrl(imageUrl)
                .build());
        var updatedUser = userRepository.save(user);
        return new AuthDTO.RegisterUserResponse(updatedUser.getId(), updatedUser.getName(),
                updatedUser.getEmail(), updatedUser.getPhoto().getImageUrl());
    }

    @Transactional(readOnly = true)
    @Override
    public AuthDTO.RegisterUserResponse findById(UUID userId) {
        var user = userRepository.findUserById(userId);
        return new AuthDTO.RegisterUserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getPhoto().getImageUrl());
    }

    @Transactional
    @Override
    public void updateUserDetails(UUID userId, AuthDTO.UpdateUserRequest updateUserRequest) {
        var user = userRepository.findUserById(userId);
        LOG.info("Attempted user details update for user: {}", user.getName());
        user.setName(updateUserRequest.name());
        user.setEmail(updateUserRequest.email());
        userRepository.save(user);
    }
}
