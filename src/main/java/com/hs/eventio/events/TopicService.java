package com.hs.eventio.events;

import com.hs.eventio.common.GlobalDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class TopicService {
    private final TopicRepository topicRepository;

    TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Transactional
    public GlobalDTO.CreateTopicResponse createTopic(GlobalDTO.CreateTopicRequest createTopicRequest) {
        var existingTopic = topicRepository.findByNameIgnoreCase(createTopicRequest.name().trim());
        if (existingTopic.isPresent()){
            throw  new RuntimeException("Topic with name: " + createTopicRequest.name() +
                    " already exists!");
        }
        var newTopic = mapTopicRequestToTopic(createTopicRequest);
        var persistedTopic = topicRepository.save(newTopic);
        return mapTopicToTopicResponse(persistedTopic);
    }

    private GlobalDTO.CreateTopicResponse mapTopicToTopicResponse(Topic topic) {
        return new  GlobalDTO.CreateTopicResponse(topic.getId(), topic.getName(), topic.getDescription(),
                topic.getTopicUrl());
    }

    private Topic mapTopicRequestToTopic(GlobalDTO.CreateTopicRequest createTopicRequest) {
        var topicUrl = "/api/v1/topics/" + createTopicRequest.name().trim().replace(" ", "-");
        return Topic.builder()
                .name(createTopicRequest.name())
                .description(createTopicRequest.description())
                .topicUrl(topicUrl)
                .build();
    }

    public GlobalDTO.CreateTopicResponse findTopicById(Long topicId) {
        var topic = topicRepository.findTopicById(topicId);
        return mapTopicToTopicResponse(topic);
    }

    @Transactional(readOnly = true)
    public GlobalDTO.PagedCollection<GlobalDTO.CreateTopicResponse> findAllTopics(Integer page, Integer pageSize) {
        var pageNo = page > 0 ? page - 1 : 0;
        var pageable = PageRequest.of(pageNo, pageSize);
        var topicsPage = topicRepository.findAll(pageable)
                .map(this::mapTopicToTopicResponse);
        return new GlobalDTO.PagedCollection<>(topicsPage.getContent(), topicsPage.getTotalElements(),
                topicsPage.getNumber() + 1, topicsPage.getTotalPages(), topicsPage.isFirst(),
                topicsPage.isLast(), topicsPage.hasNext(), topicsPage.hasPrevious());
    }
}
