package kr.pickple.back.chat.implement;

import static kr.pickple.back.chat.domain.RoomType.PERSONAL;
import static kr.pickple.back.chat.exception.ChatExceptionCode.CHAT_MEMBER_IS_NOT_IN_ROOM;
import static kr.pickple.back.chat.exception.ChatExceptionCode.CHAT_RECEIVER_NOT_FOUND;
import static kr.pickple.back.chat.exception.ChatExceptionCode.CHAT_ROOM_NOT_FOUND;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.PersonalChatRoomStatus;
import kr.pickple.back.chat.domain.RoomType;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.repository.ChatMessageRepository;
import kr.pickple.back.chat.repository.ChatRoomMemberRepository;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.chat.repository.entity.ChatMessageEntity;
import kr.pickple.back.chat.repository.entity.ChatRoomEntity;
import kr.pickple.back.chat.repository.entity.ChatRoomMemberEntity;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatReader {

    private final MemberReader memberReader;
    private final CrewRepository crewRepository;
    private final GameRepository gameRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatRoom readRoom(final Long chatRoomId) {
        final ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND, chatRoomId));

        return ChatMapper.mapChatRoomEntityToDomain(chatRoomEntity);
    }

    public ChatRoom readRoomByCrewId(final Long crewId) {
        return readRoom(crewRepository.findChatRoomId(crewId));
    }

    public ChatRoom readRoomByGameId(final Long gameId) {
        return readRoom(gameRepository.findChatRoomId(gameId));
    }

    public PersonalChatRoomStatus readPersonalRoomStatus(final Long senderId, final Long receiverId) {
        final ChatRoomMemberEntity receiverEntity = chatRoomMemberRepository.findAllByMemberId(senderId)
                .stream()
                .filter(chatRoomMemberEntity -> {
                    final ChatRoom chatRoom = readRoom(chatRoomMemberEntity.getChatRoomId());
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

    public List<ChatRoom> readEnteringRoomsByType(final Long memberId, final RoomType type) {
        return chatRoomMemberRepository.findAllByActiveTrueAndMemberId(memberId)
                .stream()
                .map(chatRoomMemberEntity -> readRoom(chatRoomMemberEntity.getChatRoomId()))
                .filter(chatRoom -> chatRoom.isMatchedRoomType(type))
                .toList();
    }

    public Member readReceiver(final Long senderId, final Long chatRoomId) {
        final ChatRoomMemberEntity receiverEntity = chatRoomMemberRepository.findByChatRoomIdAndMemberIdNot(
                        chatRoomId, senderId)
                .orElseThrow(() -> new ChatException(CHAT_RECEIVER_NOT_FOUND));

        return memberReader.readByMemberId(receiverEntity.getMemberId());
    }

    public List<Member> readRoomMembers(final Long chatRoomId) {
        return chatRoomMemberRepository.findAllByActiveTrueAndChatRoomId(chatRoomId)
                .stream()
                .map(chatRoomMember -> memberReader.readByMemberId(chatRoomMember.getMemberId()))
                .toList();
    }

    public ChatMessage readLastMessage(final ChatRoom chatRoom) {
        final ChatMessageEntity lastMessageEntity = chatMessageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(
                chatRoom.getChatRoomId());
        final Member sender = memberReader.readByMemberId(lastMessageEntity.getSenderId());

        return ChatMapper.mapChatMessageEntityToDomain(lastMessageEntity, sender, chatRoom);
    }

    public List<ChatMessage> readMessagesAfterEntrance(final Long memberId, final Long chatRoomId) {
        final ChatRoom chatRoom = readRoom(chatRoomId);

        if (!chatRoomMemberRepository.existsByActiveTrueAndChatRoomIdAndMemberId(chatRoomId, memberId)) {
            throw new ChatException(CHAT_MEMBER_IS_NOT_IN_ROOM, chatRoomId, memberId);
        }

        final LocalDateTime entranceDatetime = chatMessageRepository.findChatRoomLastEntranceMessageCreatedAt(
                memberId,
                chatRoomId
        );
        final List<ChatMessageEntity> chatMessageEntities = chatMessageRepository.findAllByChatRoomIdAndCreatedAtGreaterThanEqual(
                chatRoomId,
                entranceDatetime
        );

        return chatMessageEntities.stream()
                .map(chatMessageEntity -> ChatMapper.mapChatMessageEntityToDomain(
                        chatMessageEntity,
                        memberReader.readByMemberId(chatMessageEntity.getSenderId()),
                        chatRoom
                ))
                .toList();
    }
}
