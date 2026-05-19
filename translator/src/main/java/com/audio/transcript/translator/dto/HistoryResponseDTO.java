package com.audio.transcript.translator.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class HistoryResponseDTO {



    private String id;

    private String fileName;

    private Long fileSize;


    private String malayalamText;


    private String englishText;


    private String  status;
}
