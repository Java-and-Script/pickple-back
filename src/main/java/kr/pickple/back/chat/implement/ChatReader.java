package kr.pickple.back.chat.implement;

import static kr.pickple.back.chat.domain.RoomType.*;
import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.domain.ChatMessageDomain;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.ChatRoomDomain;
import kr.pickple.back.chat.domain.ChatRoomMember;
import kr.pickple.back.chat.domain.PersonalChatRoomStatus;
import kr.pickple.back.chat.domain.RoomType;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.repository.ChatMessageRepository;
import kr.pickple.back.chat.repository.ChatRoomMemberRepository;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatReader {

    private final MemberReader memberReader;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatRoomDomain readRoom(final Long chatRoomId) {
        final ChatRoom chatRoomEntity = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND, chatRoomId));

        return ChatMapper.mapChatRoomEntityToDomain(chatRoomEntity);
    }

    public PersonalChatRoomStatus readPersonalRoomStatus(final Long senderId, final Long receiverId) {
        final ChatRoomMember receiverEntity = chatRoomMemberRepository.findAllByMemberId(senderId)
                .stream()
                .filter(chatRoomMemberEntity -> {
                    final ChatRoomDomain chatRoom = readRoom(chatRoomMemberEntity.getChatRoomId());
                    final Long chatRoomId = chatRoom.getChatRoomId();

                    return chatRoom.isMatchedRoomType(PERSONAL)
                            && chatRoomMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, receiverId);
                })
                .findFirst()
                .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND));

        return PersonalChatRoomStatus.builder()
                .roomId(receiverEntity.getChatRoomId())
                .isSenderActive(receiverEntity.getActive())
                .build();
    }

    public List<ChatRoomDomain> readEnteringRoomsByType(final Long memberId, final RoomType type) {
        return chatRoomMemberRepository.findAllByActiveTrueAndMemberId(memberId)
                .stream()
                .map(chatRoomMemberEntity -> readRoom(chatRoomMemberEntity.getChatRoomId()))
                .filter(chatRoom -> chatRoom.isMatchedRoomType(type))
                .toList();
    }

    public MemberDomain readReceiver(final Long senderId, final Long chatRoomId) {
        final ChatRoomMember receiverEntity = chatRoomMemberRepository.findByChatRoomIdAndMemberIdNot(
                        chatRoomId, senderId)
                .orElseThrow(() -> new ChatException(CHAT_RECEIVER_NOT_FOUND));

        return memberReader.readByMemberId(receiverEntity.getMemberId());
    }

    public List<MemberDomain> readRoomMembers(final Long chatRoomId) {
        return chatRoomMemberRepository.findAllByActiveTrueAndChatRoomId(chatRoomId)
                .stream()
                .map(chatRoomMember -> memberReader.readByMemberId(chatRoomMember.getMemberId()))
                .toList();
    }

    public ChatMessageDomain readLastMessage(final ChatRoomDomain chatRoom) {
        final ChatMessage lastMessageEntity = chatMessageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(
                chatRoom.getChatRoomId());
        final MemberDomain sender = memberReader.readByMemberId(lastMessageEntity.getSenderId());

        return ChatMapper.mapChatMessageEntityToDomain(lastMessageEntity, sender, chatRoom);
    }

    public List<ChatMessageDomain> readMessagesAfterEntrance(final Long memberId, final Long chatRoomId) {
        final ChatRoomDomain chatRoom = readRoom(chatRoomId);

        if (!chatRoomMemberRepository.existsByActiveTrueAndChatRoomIdAndMemberId(chatRoomId, memberId)) {
            throw new ChatException(CHAT_MEMBER_IS_NOT_IN_ROOM, chatRoomId, memberId);
        }

        final LocalDateTime entranceDatetime = chatMessageRepository.findLastEntranceDatetimeByMemberId(memberId);
        final List<ChatMessage> chatMessageEntities = chatMessageRepository.findAllByChatRoomIdAndCreatedAtGreaterThanEqual(
                chatRoomId, entranceDatetime);

        return chatMessageEntities.stream()
                .map(chatMessageEntity -> ChatMapper.mapChatMessageEntityToDomain(
                        chatMessageEntity,
                        memberReader.readByMemberId(chatMessageEntity.getSenderId()),
                        chatRoom
                ))
                .toList();
    }
}
