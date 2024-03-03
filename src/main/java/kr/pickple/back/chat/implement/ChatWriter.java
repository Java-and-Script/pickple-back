package kr.pickple.back.chat.implement;

import static java.lang.Boolean.*;
import static kr.pickple.back.chat.domain.MessageType.*;
import static kr.pickple.back.chat.domain.RoomType.*;
import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.ChatRoomDomain;
import kr.pickple.back.chat.domain.ChatRoomMember;
import kr.pickple.back.chat.domain.MessageType;
import kr.pickple.back.chat.domain.RoomType;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.repository.ChatMessageRepository;
import kr.pickple.back.chat.repository.ChatRoomMemberRepository;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.member.domain.MemberDomain;
import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class ChatWriter {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatRoomDomain createNewPersonalRoom(final String name) {
        final ChatRoom chatRoomEntity = ChatRoom.builder()
                .name(name)
                .type(PERSONAL)
                .build();
        final ChatRoom savedChatRoomEntity = chatRoomRepository.save(chatRoomEntity);

        return ChatMapper.mapChatRoomEntityToDomain(savedChatRoomEntity);
    }

    public ChatRoomDomain createNewGroupRoom(final String name, final RoomType type, final Integer maxMemberCount) {
        final ChatRoom chatRoomEntity = ChatRoom.builder()
                .name(name)
                .type(type)
                .maxMemberCount(maxMemberCount)
                .build();
        final ChatRoom savedChatRoomEntity = chatRoomRepository.save(chatRoomEntity);

        return ChatMapper.mapChatRoomEntityToDomain(savedChatRoomEntity);
    }

    public void enterRoom(final MemberDomain member, final ChatRoomDomain chatRoom) {
        final Long memberId = member.getMemberId();
        final Long chatRoomId = chatRoom.getChatRoomId();

        if (chatRoomMemberRepository.existsByActiveTrueAndChatRoomIdAndMemberId(chatRoomId, memberId)) {
            throw new ChatException(CHAT_MEMBER_IS_ALREADY_IN_ROOM, chatRoomId, memberId);
        }

        activateRoom(chatRoomId, memberId);
        chatRoom.increaseMemberCount();
        chatRoomRepository.updateMemberCount(chatRoomId, chatRoom.getMemberCount());

        sendMessage(ENTER, MessageType.makeEnterMessage(member.getNickname()), member, chatRoom);
    }

    private void activateRoom(final Long chatRoomId, final Long memberId) {
        if (chatRoomMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, memberId)) {
            chatRoomMemberRepository.updateChatRoomMemberActiveStatus(chatRoomId, memberId, TRUE);

            return;
        }

        chatRoomMemberRepository.save(ChatRoomMember.builder()
                .memberId(memberId)
                .chatRoomId(chatRoomId)
                .build());
    }

    private void sendMessage(
            final MessageType type,
            final String content,
            final MemberDomain sender,
            final ChatRoomDomain chatRoom
    ) {
        final ChatMessage chatMessageEntity = ChatMessage.builder()
                .type(type)
                .content(content)
                .senderId(sender.getMemberId())
                .chatRoomId(chatRoom.getChatRoomId())
                .build();

        chatMessageRepository.save(chatMessageEntity);
    }
}
