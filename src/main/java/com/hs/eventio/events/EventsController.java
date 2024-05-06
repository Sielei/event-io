package com.hs.eventio.events;

import com.hs.eventio.common.GlobalDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@Tag(name = "Event", description = "Events management API")
@RestController
@RequestMapping("/api/v1/events")
class EventsController {
    private final EventService eventService;

    EventsController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Create Event", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    ResponseEntity<GlobalDTO.CreateEventResponse> createEvent(
            @RequestAttribute("userId")UUID userId,
            @Valid @RequestBody GlobalDTO.CreateEventRequest createEventRequest) {
        var event = eventService.createEvent(createEventRequest, userId);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{eventId}")
                .buildAndExpand(event.id())
                .toUri();
        return ResponseEntity.created(location).body(event);
    }

    @Operation(summary = "Find all events", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    GlobalDTO.PagedCollection<GlobalDTO.EventSummary> findAllEvents(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        return eventService.findAllEvents(page, pageSize);
    }

    @Operation(summary = "Find event by id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{eventId}")
    GlobalDTO.CreateEventResponse findEventById(@PathVariable("eventId") UUID eventId) {
        return eventService.findEventById(eventId);
    }

    @Operation(summary = "Upload event image", description = "Featured event photo",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/{eventId}/featuredPhotos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    GlobalDTO.CreateEventResponse uploadFeaturedPhoto(
            @PathVariable("eventId") UUID eventId,
            @RequestParam("featuredEventPhoto") MultipartFile featuredPhoto){
        return eventService.uploadFeaturedEventPhoto(eventId, featuredPhoto);
    }

    @Operation(summary = "Upload photos", description = "Upload event photos",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/{eventId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    GlobalDTO.CreateEventResponse uploadEventPhotos(
            @PathVariable("eventId") UUID eventId,
            @RequestPart("event_photos") List<MultipartFile> eventPhotos) {
        return eventService.uploadEventPhotos(eventId, eventPhotos);
    }

    @Operation(summary = "Create Event Ticket", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{eventId}/tickets")
    ResponseEntity<GlobalDTO.CreateEventTicketResponse> createEventTicket(
            @PathVariable("eventId") UUID eventId,
            @Valid @RequestBody GlobalDTO.CreateEventTicketRequest createEventTicketRequest) {
        var ticket = eventService.createEventTicket(eventId, createEventTicketRequest);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{ticketId}")
                .buildAndExpand(ticket.id()).toUri();
        return ResponseEntity.created(location).body(ticket);
    }

    @Operation(summary = "Get event ticket by Id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{eventId}/tickets/{ticketId}")
    GlobalDTO.CreateEventTicketResponse findEventTicketById(@PathVariable("eventId") UUID eventId,
                                                            @PathVariable("ticketId") UUID ticketId) {
        return eventService.findEventTicketById(eventId, ticketId);
    }

    @Operation(summary = "Get all event tickets", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{eventId}/tickets")
    List<GlobalDTO.CreateEventTicketResponse> findAllEventTickets(@PathVariable("eventId") UUID eventId) {
        return eventService.findAllEventTickets(eventId);
    }
}
