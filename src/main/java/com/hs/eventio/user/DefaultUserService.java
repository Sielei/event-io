package com.hs.eventio.user;

import com.hs.eventio.common.GlobalDTO;
import com.hs.eventio.common.config.EventioApplicationConfigData;
import com.hs.eventio.common.exception.UserAuthenticationException;
import com.hs.eventio.common.exception.UserRegistrationException;
import com.hs.eventio.common.service.FileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final FileUploadService fileUploadService;
    private final EventioApplicationConfigData eventioApplicationConfigData;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultUserService.class);

    DefaultUserService(UserRepository userRepository, PasswordResetRepository passwordResetRepository, FileUploadService fileUploadService, EventioApplicationConfigData eventioApplicationConfigData) {
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.fileUploadService = fileUploadService;
        this.eventioApplicationConfigData = eventioApplicationConfigData;
    }

    @Transactional
    @Override
    public GlobalDTO.RegisterUserResponse registerUser(GlobalDTO.RegisterUserRequest registerUserRequest) {
        verifyEmailIsUnique(registerUserRequest.email());
        var newUser = userRepository.save(mapRegisterUserRequestToUser(registerUserRequest));
        return mapUserToRegisterUserResponse(newUser);
    }

    private GlobalDTO.RegisterUserResponse mapUserToRegisterUserResponse(User user) {
        return new GlobalDTO.RegisterUserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getPhoto().getImageUrl());
    }

    private User mapRegisterUserRequestToUser(GlobalDTO.RegisterUserRequest registerUserRequest) {

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
    public Optional<GlobalDTO.FindUserResponse> findUserByUsername(String username) {
        return userRepository.findUserByEmail(username)
                .map(user -> {
                    var roleDto = mapRolesToRoleDtos(user.getRoles());
                    return new GlobalDTO.FindUserResponse(user.getId(), user.getName(),
                            user.getEmail(), user.getPassword(), roleDto,
                            user.getPhoto().getImageUrl());
                });
    }

    @Override
    public GlobalDTO.FindUserResponse findUserByResetToken(String token) {
        var passwordResetToken = passwordResetRepository.findByToken(token)
                .orElseThrow(() -> new UserAuthenticationException("The reset token: " + token + " is not valid!"));
        if (passwordResetToken.isUsed() || !passwordResetToken.getExpiry().after(new Date(System.currentTimeMillis()))){
            throw new UserAuthenticationException("The token: " + passwordResetToken.getToken() +
                    " is expired or already used!");
        }
        var user = passwordResetToken.getUser();
        return mapUserToFindUserResponse(user);
    }

    private GlobalDTO.FindUserResponse mapUserToFindUserResponse(User user) {
        return new GlobalDTO.FindUserResponse(user.getId(), user.getName(), user.getEmail(), user.getPassword(),
                mapRolesToRoleDtos(user.getRoles()), user.getPhoto().getImageUrl());
    }
     private Set<GlobalDTO.RoleDto> mapRolesToRoleDtos(Set<Role> roles){
        return roles.stream()
                .map(role -> new GlobalDTO.RoleDto(role.getId(), role.getName(),
                        role.getDescription(), role.getAuthority()))
                .collect(Collectors.toSet());
     }

    @Transactional
    @Override
    public void updatePassword(GlobalDTO.UpdatePasswordCommand updatePasswordCommand) {
        var user = userRepository.findUserById(updatePasswordCommand.userId());
        user.setPassword(updatePasswordCommand.newPassword());
        userRepository.save(user);
    }

    @Override
    public GlobalDTO.FindUserResponse findUserById(UUID userId) {
        var user = userRepository.findUserById(userId);
        return mapUserToFindUserResponse(user);
    }

    @Transactional
    @Override
    public void createPasswordResetToken(GlobalDTO.CreatePasswordResetTokenCommand passwordResetTokenCommand) {
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
    public GlobalDTO.RegisterUserResponse updateUserPhoto(UUID userId, MultipartFile multipartFile) {
        var user = userRepository.findUserById(userId);
        var imageType = multipartFile.getContentType();
        var imageName = fileUploadService.uploadFile(multipartFile,
                eventioApplicationConfigData.getUserPhotosUploadLocation());
        user.setPhoto(Photo.builder()
                        .imageName(imageName)
                        .imageType(imageType)
                        .imageUrl("/api/v1/users/photo/" + imageName)
                .build());
        var updatedUser = userRepository.save(user);
        return new GlobalDTO.RegisterUserResponse(updatedUser.getId(), updatedUser.getName(),
                updatedUser.getEmail(), updatedUser.getPhoto().getImageUrl());
    }

    @Transactional(readOnly = true)
    @Override
    public GlobalDTO.RegisterUserResponse findById(UUID userId) {
        var user = userRepository.findUserById(userId);
        return new GlobalDTO.RegisterUserResponse(user.getId(), user.getName(), user.getEmail(),
                user.getPhoto().getImageUrl());
    }

    @Transactional
    @Override
    public void updateUserDetails(UUID userId, GlobalDTO.UpdateUserRequest updateUserRequest) {
        var user = userRepository.findUserById(userId);
        LOG.info("Attempted user details update for user: {}", user.getName());
        user.setName(updateUserRequest.name());
        user.setEmail(updateUserRequest.email());
        userRepository.save(user);
    }
}
