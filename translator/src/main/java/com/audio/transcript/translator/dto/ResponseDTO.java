package com.audio.transcript.translator.dto;


import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDTO {

    private String fileName;
    private String contentType;
    private Long fileSize;

    private String message;


    private String malayalamText;

    private String englishText;

}
