package com.hs.eventio.events;

import com.hs.eventio.common.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface EventRepository extends JpaRepository<Event, UUID> {
    default Event findEventById(UUID eventId){
        return findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id: " + eventId
                + " does not exist!"));
    }
}