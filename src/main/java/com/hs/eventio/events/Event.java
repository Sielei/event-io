package com.hs.eventio.events;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "events")
@NoArgsConstructor @AllArgsConstructor @Builder
class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    private String title;
    private String description;
    private String slug;
    private String eventUrl;
    @Enumerated(value = EnumType.STRING)
    private EventConstants.EventStatus eventStatus;
    @Enumerated(value = EnumType.STRING)
    private EventConstants.EventLocation eventLocation;
    @Enumerated(value = EnumType.STRING)
    private EventConstants.EventCost eventCost;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EventPhoto> eventPhotos = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "event_topics",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "topics_id"))
    private Set<Topic> topics = new LinkedHashSet<>();

    private UUID host;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Ticket> tickets = new LinkedHashSet<>();
    private Instant startDate;
    private Instant endDate;
}