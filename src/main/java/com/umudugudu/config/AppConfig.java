package com.umudugudu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

/**
 * General application beans.
 * - RestTemplate for outbound HTTP calls (FCM, etc.)
 * - @EnableAsync so @Async notification dispatch works
 */
@Configuration
@EnableAsync
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
