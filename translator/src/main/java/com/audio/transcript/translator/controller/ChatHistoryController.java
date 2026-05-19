package com.audio.transcript.translator.controller;

import com.audio.transcript.translator.dto.HistoryResponseDTO;
import com.audio.transcript.translator.service.ChatHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audio")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    @GetMapping("/history")
    public ResponseEntity<List<HistoryResponseDTO>> histories(){
        return ResponseEntity.status(HttpStatus.OK).body(chatHistoryService.historyChat());
    }

}
