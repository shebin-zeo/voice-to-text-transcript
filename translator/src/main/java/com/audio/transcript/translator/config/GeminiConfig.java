package com.audio.transcript.translator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GeminiConfig {

    @Bean
    public WebClient webClient() {

        ExchangeStrategies strategies =
                ExchangeStrategies.builder()
                        .codecs(configurer ->
                                configurer.defaultCodecs()
                                        .maxInMemorySize(
                                                50 * 1024 * 1024
                                        )
                        )
                        .build();

        return WebClient.builder()
                .exchangeStrategies(strategies)
                .build();
    }
}