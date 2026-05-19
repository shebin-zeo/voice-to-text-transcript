package com.audio.transcript.translator.controller;


import com.audio.transcript.translator.dto.ResponseDTO;
import com.audio.transcript.translator.service.GeminiService;
import com.audio.transcript.translator.service.TranscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/audio")
@RequiredArgsConstructor
public class AudioController {


    private final TranscriptionService transcriptionService;

    private final GeminiService service;


    @PostMapping("/transcribe")
    public ResponseEntity<ResponseDTO> transcribe(
            @RequestParam("file")MultipartFile file
            )
    {
//        String response=transcriptionService.processAudio(file);

        return ResponseEntity.status(HttpStatus.OK).body(service.transcribeAudio(file));
    }
}
