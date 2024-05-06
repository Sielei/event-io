package com.hs.eventio.events;

import com.hs.eventio.common.GlobalDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface EventService {
    GlobalDTO.CreateEventResponse createEvent(GlobalDTO.CreateEventRequest createEventRequest, UUID userId);

    GlobalDTO.CreateEventResponse uploadEventPhotos(UUID eventId, List<MultipartFile> eventPhotos);

    GlobalDTO.CreateEventResponse findEventById(UUID eventId);

    GlobalDTO.PagedCollection<GlobalDTO.EventSummary> findAllEvents(Integer page, Integer pageSize);

    GlobalDTO.CreateEventTicketResponse createEventTicket(UUID eventId, GlobalDTO.CreateEventTicketRequest createEventTicketRequest);

    GlobalDTO.CreateEventTicketResponse findEventTicketById(UUID eventId, UUID ticketId);

    List<GlobalDTO.CreateEventTicketResponse> findAllEventTickets(UUID eventId);

    GlobalDTO.CreateEventResponse uploadFeaturedEventPhoto(UUID eventId, MultipartFile featuredPhoto);
}
