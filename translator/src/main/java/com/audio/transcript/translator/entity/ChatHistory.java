package com.audio.transcript.translator.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String fileName;

    private Long fileSize;

    @Column(columnDefinition = "TEXT")
    private String malayalamText;


    @Column(columnDefinition = "TEXT")
    private String englishText;


    private String  status;
}
