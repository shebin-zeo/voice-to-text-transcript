package com.audio.transcript.translator.service;

import com.audio.transcript.translator.dto.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TranscriptionService {


    public ResponseDTO processAudio(MultipartFile file)
    {
        try {

            return ResponseDTO.builder()
                    .fileName(file.getName())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .message("Audio file is received successfully ")
                    .build();


        }

        catch (Exception e){
            return ResponseDTO.builder().message("Have issue with the backend").build();
        }
    }
}
