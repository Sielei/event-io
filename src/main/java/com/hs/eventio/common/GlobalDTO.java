package com.hs.eventio.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GlobalDTO {

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
    public record CreateEventRequest(String title, String description, String slug, Instant startDate,
                                     Instant endDate, boolean isPhysicalEvent,
                                     boolean isFreeEvent, List<Long> topics, UUID venueId){}
    public record CreateTopicRequest(String name, String description){}
    public record CreateTopicResponse(Long id, String name, String description, String topicUrl){}
    public record TopicDto(Long id, String name, String description, String topicUrl){}
    public record EventPhotoDto(Long id, String imageType, String imageUrl){}
    public record HostDto(UUID id, String name, String email, String imageUrl){}
    public record Attendee(UUID id, String name, String email, String imageUrl){}
    public record CreateEventResponse(UUID id, String title, String description, String slug, String eventLocation,
                                      String eventCost, List<TopicDto> topics, List<EventPhotoDto> featuredPhotos,
                                      HostDto host, List<EventPhotoDto> eventPhotos){}
    public record EventSummary(List<EventPhotoDto> featuredPhotos, String title, String eventLocation,
                               String eventCost, Instant startDate){}
    public enum Currency {
        EUR, GBP, KSH, USD
    }
    public record CreateEventTicketRequest(String title, Integer numberOfTickets, Currency currency,
                                           BigDecimal price, Date ticketClose){}
    public record CreateEventTicketResponse(UUID id, String title, Integer numberOfTickets,
                                            Integer numberOfPurchasedTickets, Currency currency,
                                            BigDecimal price, Date ticketClose){}

    public record PagedCollection<T>(List<T> data, long totalElements, Integer pageNumber, Integer totalPages,
                                     @JsonProperty("isFirst") boolean isFirst, @JsonProperty("isLast") boolean isLast,
                                     @JsonProperty("hasNext") boolean hasNext, @JsonProperty("hasPrevious") boolean hasPrevious){}
}
