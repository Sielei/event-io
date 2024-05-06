package com.hs.eventio.events;

import com.hs.eventio.common.GlobalDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Topics", description = "Event topics")
@RestController
@RequestMapping("/api/v1/topics")
class TopicsController {
    private final TopicService topicService;

    public TopicsController(TopicService topicService) {
        this.topicService = topicService;
    }

    @Operation(summary = "Create event topic", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    ResponseEntity<GlobalDTO.CreateTopicResponse> createEventTopic(
            @Valid @RequestBody GlobalDTO.CreateTopicRequest createTopicRequest){
        var newTopic = topicService.createTopic(createTopicRequest);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newTopic.id())
                .toUri();
        return ResponseEntity.created(location).body(newTopic);
    }

    @Operation(summary = "Find topic by Id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{topicId}")
    public GlobalDTO.CreateTopicResponse findTopicById(@PathVariable("topicId") Long topicId){
        return topicService.findTopicById(topicId);
    }

    @Operation(summary = "Find all topics", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    GlobalDTO.PagedCollection<GlobalDTO.CreateTopicResponse> findAllTopics(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
        return topicService.findAllTopics(page, pageSize);
    }

}
