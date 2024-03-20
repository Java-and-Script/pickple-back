package kr.pickple.back.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.chat.repository.entity.ChatRoomMemberEntity;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMemberEntity, Long> {

    Boolean existsByChatRoomIdAndMemberId(final Long chatRoomId, final Long memberId);

    Boolean existsByActiveTrueAndChatRoomIdAndMemberId(final Long chatRoomId, final Long memberId);

    List<ChatRoomMemberEntity> findAllByMemberId(final Long memberId);

    List<ChatRoomMemberEntity> findAllByActiveTrueAndMemberId(final Long memberId);

    List<ChatRoomMemberEntity> findAllByActiveTrueAndChatRoomId(final Long chatRoomId);

    // 개인 채팅방에서 상대방에 대한 ChatRoomMember 데이터 조회용
    Optional<ChatRoomMemberEntity> findByChatRoomIdAndMemberIdNot(final Long chatRoomId, final Long memberId);

    @Modifying(clearAutomatically = true)
    @Query("""
            update ChatRoomMemberEntity crm 
            set crm.active = :activeStatus 
            where crm.chatRoomId = :chatRoomId and crm.memberId = :memberId""")
    void updateChatRoomMemberActiveStatus(
            final Long chatRoomId,
            final Long memberId,
            final Boolean activeStatus
    );
}
