package com.hs.eventio.events;

import com.hs.eventio.common.GlobalDTO;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tickets")
@NoArgsConstructor @AllArgsConstructor @Builder
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    private String title;
    private Integer numberOfTickets;
    private Integer numberOfPurchasedTickets;
    @Enumerated(value = EnumType.STRING)
    private GlobalDTO.Currency currency;
    private BigDecimal price;
    private Date ticketClose;

}