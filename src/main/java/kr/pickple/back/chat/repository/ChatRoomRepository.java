package kr.pickple.back.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.chat.repository.entity.ChatRoomEntity;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    @Query("update ChatRoomEntity cr set cr.memberCount = :memberCount where cr.id = :chatRoomId")
    void updateMemberCount(final Long chatRoomId, final Integer memberCount);
}
