package kr.pickple.back.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.chat.domain.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

}
