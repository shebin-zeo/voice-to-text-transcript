package com.audio.transcript.translator.service;


import com.audio.transcript.translator.dto.HistoryResponseDTO;
import com.audio.transcript.translator.entity.ChatHistory;
import com.audio.transcript.translator.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatHistoryService {

    private final ChatRepository chatRepository;

    public List<HistoryResponseDTO> historyChat()
    {

       List<ChatHistory> chatHistory=chatRepository.findAll();

        return chatHistory.stream().map(
                history->
                HistoryResponseDTO.builder()
                        .id(history.getId())
                        .fileName(history.getFileName())
                        .fileSize(history.getFileSize())
                        .malayalamText(history.getMalayalamText())
                        .englishText(history.getEnglishText())
                        .status(history.getStatus())
                        .build()
        ).toList();
    }
}
