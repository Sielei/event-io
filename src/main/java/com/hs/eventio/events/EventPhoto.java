package com.hs.eventio.events;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "event_photos")
@NoArgsConstructor @AllArgsConstructor @Builder
class EventPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @Enumerated(value = EnumType.STRING)
    private EventConstants.EventPhotoType eventPhotoType;
    private String imageType;
    private String imageName;
    private String imageUrl;
}