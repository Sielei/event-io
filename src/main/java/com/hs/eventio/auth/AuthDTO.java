package com.hs.eventio.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.UUID;

public class AuthDTO {

    public record RegisterUserResponse(UUID id, String name, String email, String photoUrl){}
    public record RegisterUserRequest(String name, String email, String password){}
    public record LoginResponse(String token, UUID id, String name, String email, String photoUrl){}
    public record LoginRequest(String email, String password){}
    public record GetResetTokenResponse(String success, String message){}
    public record GetResetTokenRequest(String email){}
    public record FindUserResponse(UUID id, String name, String email, String password, List<String> roles,
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
    public record UpdatePasswordRequest(String currentPassword, String newPassword) {}
}
