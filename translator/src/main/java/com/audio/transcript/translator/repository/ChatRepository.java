package com.audio.transcript.translator.repository;

import com.audio.transcript.translator.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository  extends JpaRepository<ChatHistory,String> {
}
