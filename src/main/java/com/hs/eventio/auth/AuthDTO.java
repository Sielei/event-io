package com.hs.eventio.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Set;
import java.util.UUID;

public class AuthDTO {

    public record RegisterUserResponse(UUID id, String name, String email, String photoUrl){}
    public record RegisterUserRequest(@NotBlank(message = "Name is required") String name,
                                      @Email(message = "Email should be valid") String email,
                                      @NotBlank(message = "Password is required") String password){}
    public record LoginResponse(String token, UUID id, String name, String email, String photoUrl){}
    public record LoginRequest(@Email(message = "Email should be valid") String email,
                               @NotBlank(message = "Password is required") String password){}
    public record GetResetTokenResponse(String success, String message){}
    public record GetResetTokenRequest(@Email(message = "Email should be valid") String email){}
    public record RoleDto(Long id, String name, String description, String authority){}
    public record FindUserResponse(UUID id, String name, String email, String password, Set<RoleDto> roles,
                                   String photoUrl){}
    public record CreatePasswordResetTokenCommand(UUID userId, String token) {
        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private UUID userId;
            private String token;

            private Builder() {
            }

            public Builder userId(UUID val) {
                userId = val;
                return this;
            }

            public Builder token(String val) {
                token = val;
                return this;
            }

            public CreatePasswordResetTokenCommand build() {
                return new CreatePasswordResetTokenCommand(userId, token);
            }
        }
    }
    public record ResetPasswordRequest(@NotBlank(message = "Reset token is required")
                                       @Pattern(message = "Invalid password reset token",
                                               regexp = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")
                                       String token,
                                       @NotBlank(message = "New password is required")
                                       String newPassword) {}
    public record UpdatePasswordCommand(UUID userId, String newPassword) {}
    public record RefreshToken(String token) {}
    public record UpdatePasswordRequest(@NotBlank(message = "Current password is required") String currentPassword,
                                        @NotBlank(message = "New password is required")String newPassword) {}
    public record UpdateUserRequest(@NotBlank(message = "Name is required") String name,
                                    @Email(message = "Email should be valid") String email){}
}
