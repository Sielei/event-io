package com.hs.eventio.events;

import com.hs.eventio.common.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Optional<Ticket> findByIdAndEvent(UUID ticketId, Event event);
    List<Ticket> findByEventId(UUID eventId);
    default Ticket getTicketByIdAndEvent(Event event, UUID ticketId){
        return findByIdAndEvent(ticketId, event)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket for event: " + event.getTitle()
                + " with id: " + ticketId + " does not exist!"));
    }
}