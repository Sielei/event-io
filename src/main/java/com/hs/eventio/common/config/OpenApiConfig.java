package com.hs.eventio.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openAPI(){
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Events IO API")
                        .description("Events IO is an api that allows users to create events and meetup with people of similar interests.")
                        .version("1.0.0")
                        .contact(new Contact().name("Sielei Herman").email("hsielei@gmail.com")));
    }
}
