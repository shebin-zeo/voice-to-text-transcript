package com.audio.transcript.translator.service;

import com.audio.transcript.translator.config.VertexTokenProvider;
import com.audio.transcript.translator.dto.ResponseDTO;
import com.audio.transcript.translator.entity.ChatHistory;
import com.audio.transcript.translator.repository.ChatRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import java.io.File;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final WebClient webClient;
//    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectMapper objectMapper;

    private final VertexTokenProvider tokenProvider;

    private final ChatRepository chatRepository;

    @Value("${gemini.api.key}")
    private String apiKey;

//    @Value("${gemini.api.url}")
//    private String url;





//    For vertex ai
    @Value("${vertex.project.id}")
    private String projectId;

    @Value("${vertex.location}")
    private String location;

    @Value("${vertex.model}")
    private String model;



    public ResponseDTO transcribeAudio(MultipartFile file) {

        try {
            String base64Audio = Base64.getEncoder()
                    .encodeToString(file.getBytes());

            String prompt = """
                    You are an expert Malayalam audio transcriber.

                    Tasks:
                    1. Transcribe Malayalam audio accurately.
                    2. Translate it into professional English.

                    Return ONLY valid JSON in this exact format:
                    {
                      "malayalamText": "...",
                      "englishText": "..."
                    }
                    """;

//            Map<String, Object> requestBody = Map.of(
//                    "contents", new Object[]{
//                            Map.of(
//                                    "parts", new Object[]{
//                                            Map.of("text", prompt),
//                                            Map.of(
//                                                    "inline_data",
//                                                    Map.of(
//                                                            "mime_type", file.getContentType(),
//                                                            "data", base64Audio
//                                                    )
//                                            )
//                                    }
//                            )
//                    }
//            );

            Map<String, Object> requestBody = Map.of(
                    "contents",
                    List.of(
                            Map.of(
                                    "role", "user",
                                    "parts",
                                    List.of(
                                            Map.of(
                                                    "text",
                                                    prompt
                                            ),
                                            Map.of(
                                                    "inlineData",
                                                    Map.of(
                                                            "mimeType",
                                                            file.getContentType(),
                                                            "data",
                                                            base64Audio
                                                    )
                                            )
                                    )
                            )
                    )
            );

//            Map<?, ?> response = webClient.post()
//                    .uri(url + apiKey)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .bodyValue(requestBody)
//                    .retrieve()
//                    .bodyToMono(Map.class)
//                    .timeout(Duration.ofMinutes(2))
//                    .retry(3)
//                    .block();



            log.info("projectId={}", projectId);
            log.info("location={}", location);
            log.info("model={}", model);
            String token = tokenProvider.getAccessToken();
//            log.info("Token is : {} ",token);

            String vertexUrl =
                    String.format(
                            "https://%s-aiplatform.googleapis.com/v1/projects/%s/locations/%s/publishers/google/models/%s:generateContent",
                            location,
                            projectId,
                            location,
                            model
                    );


            Map<?, ?> response = webClient.post()
                    .uri(vertexUrl)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
//                    For better error handling, we can check for non-2xx status and extract error details
                    .onStatus(
                            status -> status.isError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                     .flatMap(errorBody -> {
                                        log.error("Google Error: {}", errorBody);
                                        return Mono.error(new RuntimeException(errorBody));
                                    })
                    )
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Empty response from Vertex AI");
            }

            // Token usage tracking input and output tokens
            int promptTokens = 0;
            int outputTokens = 0;
            int totalTokens = 0;

            Object usageObj = response.get("usageMetadata");

            if (usageObj instanceof Map<?, ?> usageMetadata) {

                Object promptObj =
                        usageMetadata.get("promptTokenCount");

                Object outputObj =
                        usageMetadata.get("candidatesTokenCount");

                Object totalObj =
                        usageMetadata.get("totalTokenCount");

                promptTokens =
                        promptObj instanceof Number n
                                ? n.intValue()
                                : 0;

                outputTokens =
                        outputObj instanceof Number n
                                ? n.intValue()
                                : 0;

                totalTokens =
                        totalObj instanceof Number n
                                ? n.intValue()
                                : 0;
            }

            /*
             * Estimate prompt-only tokens
             * Gemini roughly uses ~4 chars/token for English.
             */
            int promptTextTokens =
                    Math.max(prompt.length() / 4, 0);

            /*
             * Estimated audio tokens
             */
            int audioTokens =
                    Math.max(
                            promptTokens - promptTextTokens,
                            0
                    );


//            double audioSizeKB =
//                    file.getSize() / 1024.0;
//
//            double audioSizeMB =
//                    file.getSize() / (1024.0 * 1024.0);


            double audioLength =
                    getAudioLength(file);

            log.info(
                    """
                    
                    Audio Length : {} sec
                    Audio Token  : {}
                    Prompt Token : {}
                    Output Token : {}
                    Total Token : {}
                    
                    """,
                    String.format("%.2f", audioLength),
                    audioTokens,
                    promptTokens,
                    outputTokens,
                    totalTokens
            );

            String output = extractText(response);
            log.info("Gemini output: {}", output);

            TranscriptParts transcript = parseTranscript(output);

//            For saving the history to DB
            ChatHistory chatHistory=ChatHistory.builder()
                    .fileName(file.getOriginalFilename())
                    .malayalamText(transcript.malayalamText())
                    .englishText(transcript.englishText())
                    .fileSize(file.getSize())
                    .status("Successfully Processed Your Request")
                    .promptTokenCount(promptTokens)
                    .outputTokenCount(outputTokens)
                    .totalTokenCount(totalTokens)
                    .build();

            chatRepository.save(chatHistory);



            return ResponseDTO.builder()
                    .fileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .message("Successfully Processed Your Request")
                    .malayalamText(transcript.malayalamText())
                    .englishText(transcript.englishText())
                    .build();



        } catch (Exception ex) {
            log.error("Audio transcription failed", ex);
            throw new RuntimeException(
                    ex.getMessage() != null ? ex.getMessage() : "Failed to process audio with Gemini AI."
            );
        }
    }

    private String extractText(Map<?, ?> response) {

        if (response == null) {
            throw new RuntimeException("Empty Gemini response");
        }

        List<?> candidates = (List<?>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("No candidates returned from Gemini");
        }

        Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
        Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
        if (content == null) {
            throw new RuntimeException("No content found in Gemini response");
        }

        List<?> parts = (List<?>) content.get("parts");
        if (parts == null || parts.isEmpty()) {
            throw new RuntimeException("No parts found in Gemini response");
        }

        Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);
        Object text = firstPart.get("text");
        if (text == null) {
            throw new RuntimeException("No text returned from Gemini");
        }

        return text.toString();
    }

    private TranscriptParts parseTranscript(String output) {

        String cleaned = stripCodeFences(output).trim();

        // Preferred: strict JSON
        try {
            JsonNode node = objectMapper.readTree(cleaned);

            String malayalam = text(node, "malayalamText");
            String english = text(node, "englishText");

            if (!malayalam.isBlank() || !english.isBlank()) {
                return new TranscriptParts(malayalam, english);
            }
        } catch (Exception ignore) {
            // fallback below
        }

        // Fallback: legacy "Malayalam:" / "English:" format
        if (cleaned.contains("English:")) {
            String[] parts = cleaned.split("English:", 2);

            String malayalam = parts[0]
                    .replace("Malayalam:", "")
                    .trim();

            String english = parts.length > 1 ? parts[1].trim() : "";

            if (!malayalam.isBlank() || !english.isBlank()) {
                return new TranscriptParts(malayalam, english);
            }
        }

        throw new RuntimeException("Invalid Gemini response format");
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value != null && !value.isNull() ? value.asText("").trim() : "";
    }

    private String stripCodeFences(String text) {
        if (text == null) {
            return "";
        }

        String cleaned = text.trim();

        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceAll("^```(?:json)?\\s*", "");
            cleaned = cleaned.replaceAll("\\s*```$", "");
        }

        return cleaned.trim();
    }

    private record TranscriptParts(String malayalamText, String englishText) {}


//For Getting the audio length in seconds using JAVE library
    private double getAudioLength(MultipartFile file) {

        File tempFile = null;

        try {

            String extension =
                    file.getOriginalFilename()
                            .substring(
                                    file.getOriginalFilename().lastIndexOf(".")
                            );

            tempFile =
                    File.createTempFile("audio-", extension);

            file.transferTo(tempFile);

            MultimediaObject multimediaObject =
                    new MultimediaObject(tempFile);

            MultimediaInfo info =
                    multimediaObject.getInfo();

            return info.getDuration() / 1000.0;

        } catch (Exception ex) {

            log.error("Failed to get audio duration", ex);

            return 0;

        } finally {

            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }
}