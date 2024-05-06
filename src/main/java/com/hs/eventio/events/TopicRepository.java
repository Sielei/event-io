package com.hs.eventio.events;

import com.hs.eventio.common.exception.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findByNameIgnoreCase(String name);
    default Topic findTopicById(Long topicId){
        return findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic with id: " + topicId +
                        " cannot be found!"));
    }
}