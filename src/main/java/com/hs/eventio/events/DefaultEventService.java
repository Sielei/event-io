package com.hs.eventio.events;

import com.hs.eventio.common.GlobalDTO;
import com.hs.eventio.common.config.EventioApplicationConfigData;
import com.hs.eventio.common.service.FileUploadService;
import com.hs.eventio.user.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
class DefaultEventService implements EventService {
    private final TopicRepository topicRepository;
    private final EventRepository eventRepository;
    private final EventPhotoRepository eventPhotoRepository;
    private final UserService userService;
    private final FileUploadService fileUploadService;
    private final EventioApplicationConfigData eventioApplicationConfigData;
    private final TicketRepository ticketRepository;

    public DefaultEventService(TopicRepository topicRepository, EventRepository eventRepository,
                               EventPhotoRepository eventPhotoRepository, UserService userService,
                               FileUploadService fileUploadService,
                               EventioApplicationConfigData eventioApplicationConfigData,
                               TicketRepository ticketRepository) {
        this.topicRepository = topicRepository;
        this.eventRepository = eventRepository;
        this.eventPhotoRepository = eventPhotoRepository;
        this.userService = userService;
        this.fileUploadService = fileUploadService;
        this.eventioApplicationConfigData = eventioApplicationConfigData;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    @Override
    public GlobalDTO.CreateEventResponse createEvent(GlobalDTO.CreateEventRequest createEventRequest,
                                                     UUID userId) {
        var event = mapEventRequestToEvent(createEventRequest, userId);
        var persistedEvent = eventRepository.save(event);
        return mapEventToEventResponse(persistedEvent);
    }

    @Transactional
    @Override
    public GlobalDTO.CreateEventResponse uploadEventPhotos(UUID eventId, List<MultipartFile> eventPhotos) {
        var event = eventRepository.findEventById(eventId);
        var photos = uploadPhotos(eventPhotos, event, EventConstants.EventPhotoType.OTHER);
        eventPhotoRepository.saveAll(photos);
        return mapEventToEventResponse(event);
    }

    @Transactional(readOnly = true)
    @Override
    public GlobalDTO.CreateEventResponse findEventById(UUID eventId) {
        var event = eventRepository.findEventById(eventId);
        return mapEventToEventResponse(event);
    }

    @Transactional(readOnly = true)
    @Override
    public GlobalDTO.PagedCollection<GlobalDTO.EventSummary> findAllEvents(Integer page, Integer pageSize) {
        var pageNo = page > 0 ? page - 1 : 0;
        var pageable = PageRequest.of(pageNo, pageSize);
        var eventsPage = eventRepository.findAll(pageable)
                .map(this::mapEventToEventSummary);
        return new GlobalDTO.PagedCollection<>(
                eventsPage.getContent(),
                eventsPage.getTotalElements(),
                eventsPage.getNumber() + 1,
                eventsPage.getTotalPages(),
                eventsPage.isFirst(),
                eventsPage.isLast(),
                eventsPage.hasNext(),
                eventsPage.hasPrevious()
        );
    }

    @Transactional
    @Override
    public GlobalDTO.CreateEventTicketResponse createEventTicket(
            UUID eventId, GlobalDTO.CreateEventTicketRequest createEventTicketRequest) {
        var event = eventRepository.findEventById(eventId);
        if (event.getEventCost().equals(EventConstants.EventCost.FREE)){
            throw new RuntimeException("Cannot create ticket for a free event: " + event.getTitle() + "!");
        }
        var ticket = mapTicketRequestToTicket(event, createEventTicketRequest);
        var persistedTicket = ticketRepository.save(ticket);
        return mapTicketToTicketResponse(persistedTicket);
    }

    @Transactional(readOnly = true)
    @Override
    public GlobalDTO.CreateEventTicketResponse findEventTicketById(UUID eventId, UUID ticketId) {
        var event = eventRepository.findEventById(eventId);
        var ticket = ticketRepository.getTicketByIdAndEvent(event, ticketId);
        return mapTicketToTicketResponse(ticket);
    }

    @Transactional(readOnly = true)
    @Override
    public List<GlobalDTO.CreateEventTicketResponse> findAllEventTickets(UUID eventId) {
        var eventTickets = ticketRepository.findByEventId(eventId);
        return eventTickets.stream().map(this::mapTicketToTicketResponse).toList();
    }

    @Transactional
    @Override
    public GlobalDTO.CreateEventResponse uploadFeaturedEventPhoto(UUID eventId, MultipartFile featuredPhoto) {
        var event = eventRepository.findEventById(eventId);
        var imageType = featuredPhoto.getContentType();
        var imageName = fileUploadService.uploadFile(featuredPhoto,
                eventioApplicationConfigData.getEventPhotosUploadLocation());
        var photo = EventPhoto.builder()
                .event(event)
                .eventPhotoType(EventConstants.EventPhotoType.FEATURED)
                .imageType(imageType)
                .imageName(imageName)
                .imageUrl("/api/v1/events/photos/" + imageName)
                .build();
        var persistedPhoto = eventPhotoRepository.save(photo);
        return mapEventToEventResponse(persistedPhoto.getEvent());
    }

    private GlobalDTO.CreateEventTicketResponse mapTicketToTicketResponse(Ticket ticket) {
        return new GlobalDTO.CreateEventTicketResponse(ticket.getId(), ticket.getTitle(),
                ticket.getNumberOfTickets(), ticket.getNumberOfPurchasedTickets(), ticket.getCurrency(),
                ticket.getPrice(), ticket.getTicketClose());
    }

    private Ticket mapTicketRequestToTicket(Event event,
                                            GlobalDTO.CreateEventTicketRequest createEventTicketRequest) {
        return Ticket.builder()
                .event(event)
                .title(createEventTicketRequest.title())
                .numberOfTickets(createEventTicketRequest.numberOfTickets())
                .currency(createEventTicketRequest.currency())
                .price(createEventTicketRequest.price())
                .ticketClose(createEventTicketRequest.ticketClose())
                .build();

    }

    private GlobalDTO.EventSummary mapEventToEventSummary(Event event) {
        var featuredPhotos = event.getEventPhotos().stream()
                .filter(eventPhoto -> eventPhoto.getEventPhotoType() == EventConstants.EventPhotoType.FEATURED)
                .map(eventPhoto -> new GlobalDTO.EventPhotoDto(eventPhoto.getId(), eventPhoto.getImageType(),
                        eventPhoto.getImageUrl()))
                .toList();
        return new GlobalDTO.EventSummary(featuredPhotos, event.getTitle(), event.getEventLocation().toString(),
                event.getEventCost().toString(), event.getStartDate());
    }

    private List<EventPhoto> uploadPhotos(List<MultipartFile> featuredPhotos,
                                                  Event event, EventConstants.EventPhotoType photoType) {
        return featuredPhotos.stream()
                .map(photo -> {
                    var fileType = photo.getContentType();
                    var photoName = fileUploadService.uploadFile(photo,
                            eventioApplicationConfigData.getEventPhotosUploadLocation());
                    return EventPhoto.builder()
                            .event(event)
                            .eventPhotoType(photoType)
                            .imageType(fileType)
                            .imageName(photoName)
                            .imageUrl("/api/v1/events/photos/" + photoName)
                            .build();
                })
                .toList();
    }

    private GlobalDTO.CreateEventResponse mapEventToEventResponse(Event event) {
        var topics = event.getTopics().stream().map(topic -> new GlobalDTO.TopicDto(topic.getId(),
                topic.getName(), topic.getDescription(), topic.getTopicUrl()))
                .toList();
        var featuredPhotos = event.getEventPhotos() != null ? event.getEventPhotos().stream()
                .filter(eventPhoto -> eventPhoto.getEventPhotoType() == EventConstants.EventPhotoType.FEATURED)
                .map(eventPhoto -> new GlobalDTO.EventPhotoDto(
                        eventPhoto.getId(), eventPhoto.getImageType(), eventPhoto.getImageUrl()))
                .toList(): Collections.EMPTY_LIST;
        var eventPhotos = event.getEventPhotos() != null ? event.getEventPhotos().stream()
                .filter(eventPhoto -> eventPhoto.getEventPhotoType() == EventConstants.EventPhotoType.OTHER)
                .map(eventPhoto -> new GlobalDTO.EventPhotoDto(
                        eventPhoto.getId(), eventPhoto.getImageType(), eventPhoto.getImageUrl()))
                .toList(): Collections.EMPTY_LIST;
        var host = userService.findUserById(event.getHost());
        var eventHost = new GlobalDTO.HostDto(host.id(), host.name(), host.email(), host.photoUrl());
        return new GlobalDTO.CreateEventResponse(event.getId(), event.getTitle(), event.getDescription(),
                event.getSlug(), event.getEventLocation().toString(), event.getEventCost().toString(),
                event.getEventAttendance().toString(), event.getAttendanceLimit(), topics, featuredPhotos,
                eventHost, eventPhotos);
    }

    private Event mapEventRequestToEvent(GlobalDTO.CreateEventRequest createEventRequest, UUID userId) {
        var eventLocation = createEventRequest.isPhysicalEvent() ? EventConstants.EventLocation.PHYSICAL : EventConstants.EventLocation.VIRTUAL;
        var eventCost = createEventRequest.isFreeEvent() ? EventConstants.EventCost.FREE : EventConstants.EventCost.PAID;
        var eventAttendance = createEventRequest.isAttendanceLimited() ? EventConstants.EventAttendance.LIMITED : EventConstants.EventAttendance.UNLIMITED;
        var topics = createEventRequest.topics().stream()
                .map(topicRepository::findTopicById)
                .collect(Collectors.toSet());
        return Event.builder()
                .title(createEventRequest.title())
                .description(createEventRequest.description())
                .slug(createEventRequest.slug())
                .eventStatus(EventConstants.EventStatus.ACTIVE)
                .eventLocation(eventLocation)
                .eventCost(eventCost)
                .eventAttendance(eventAttendance)
                .attendanceLimit(createEventRequest.attendanceLimit())
                .topics(topics)
                .host(userId)
                .startDate(createEventRequest.startDate())
                .endDate(createEventRequest.endDate())
                .build();
    }
}
