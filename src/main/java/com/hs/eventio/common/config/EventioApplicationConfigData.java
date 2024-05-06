package com.hs.eventio.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "eventio-config")
public class EventioApplicationConfigData {
    private String userPhotosUploadLocation;
    private String  eventPhotosUploadLocation;
}
