package com.hs.eventio.user;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@Embeddable
@AllArgsConstructor @NoArgsConstructor @Builder
class Photo {
  private String imageType;
  private String imageName;
  private String imageUrl;
}