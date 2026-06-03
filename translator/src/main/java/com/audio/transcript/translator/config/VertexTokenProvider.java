package com.audio.transcript.translator.config;

import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class VertexTokenProvider {

    public String getAccessToken() {

        try {

            GoogleCredentials credentials =
                    GoogleCredentials.getApplicationDefault()
                            .createScoped(
                                    List.of("https://www.googleapis.com/auth/cloud-platform")
                            );

            credentials.refreshIfExpired();
            log.info("Token from the Google is : {}",credentials.getAccessToken().getTokenValue());


            return credentials.getAccessToken().getTokenValue();



        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate Vertex token", ex);
        }
    }
}