package kr.pickple.back.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.chat.domain.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}
