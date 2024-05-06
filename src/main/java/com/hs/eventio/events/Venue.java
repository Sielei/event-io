package com.hs.eventio.events;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "venues")
@AllArgsConstructor @NoArgsConstructor @Builder
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    private String name;
    private String country;
    private String state;
    private String city;
    private String address;
    private String latitude;
    private String longitude;

}