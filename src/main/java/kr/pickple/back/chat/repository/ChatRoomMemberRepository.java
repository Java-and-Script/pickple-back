package kr.pickple.back.chat.repository;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.chat.domain.ChatRoomMember;
import kr.pickple.back.chat.exception.ChatException;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    Boolean existsByChatRoomIdAndMemberId(final Long chatRoomId, final Long memberId);

    Boolean existsByActiveTrueAndChatRoomIdAndMemberId(final Long chatRoomId, final Long memberId);

    List<ChatRoomMember> findAllByMemberId(final Long memberId);

    List<ChatRoomMember> findAllByActiveTrueAndMemberId(final Long memberId);

    List<ChatRoomMember> findAllByActiveTrueAndChatRoomId(final Long chatRoomId);

    Optional<ChatRoomMember> findByMemberIdAndChatRoomId(final Long memberId, final Long chatRoomId);

    // 개인 채팅방에서 상대방에 대한 ChatRoomMember 데이터 조회용
    Optional<ChatRoomMember> findByChatRoomIdAndMemberIdNot(final Long chatRoomId, final Long memberId);

    default ChatRoomMember getByMemberIdAndChatRoomId(final Long memberId, final Long chatRoomId) {
        return findByMemberIdAndChatRoomId(memberId, chatRoomId)
                .orElseThrow(() -> new ChatException(CHAT_MEMBER_IS_NOT_IN_ROOM, memberId, chatRoomId));
    }

    default ChatRoomMember getPersonalChatRoomReceiver(final Long chatRoomId, final Long senderId) {
        return findByChatRoomIdAndMemberIdNot(chatRoomId, senderId)
                .orElseThrow(() -> new ChatException(CHAT_RECEIVER_NOT_FOUND));
    }
}
