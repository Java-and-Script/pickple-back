package kr.pickple.back.chat.implement;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static kr.pickple.back.chat.domain.MessageType.ENTER;
import static kr.pickple.back.chat.domain.MessageType.LEAVE;
import static kr.pickple.back.chat.domain.RoomType.CREW;
import static kr.pickple.back.chat.domain.RoomType.GAME;
import static kr.pickple.back.chat.domain.RoomType.PERSONAL;
import static kr.pickple.back.chat.exception.ChatExceptionCode.CHAT_CREW_CHATROOM_NOT_ALLOWED_TO_LEAVE;
import static kr.pickple.back.chat.exception.ChatExceptionCode.CHAT_GAME_CHATROOM_NOT_ALLOWED_TO_LEAVE;
import static kr.pickple.back.chat.exception.ChatExceptionCode.CHAT_MEMBER_IS_ALREADY_IN_ROOM;
import static kr.pickple.back.chat.exception.ChatExceptionCode.CHAT_MEMBER_IS_NOT_IN_ROOM;
import static kr.pickple.back.common.domain.RegistrationStatus.CONFIRMED;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.MessageType;
import kr.pickple.back.chat.domain.RoomType;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.repository.ChatMessageRepository;
import kr.pickple.back.chat.repository.ChatRoomMemberRepository;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.chat.repository.entity.ChatMessageEntity;
import kr.pickple.back.chat.repository.entity.ChatRoomEntity;
import kr.pickple.back.chat.repository.entity.ChatRoomMemberEntity;
import kr.pickple.back.common.util.DateTimeUtil;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.member.domain.Member;
import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class ChatWriter {

    private static final Integer PERSONAL_ROOM_MAX_MEMBER_COUNT = 2;

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final GameRepository gameRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatRoom createNewPersonalRoom(final String name) {
        final ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .name(name)
                .type(PERSONAL)
                .maxMemberCount(PERSONAL_ROOM_MAX_MEMBER_COUNT)
                .build();
        final ChatRoomEntity savedChatRoomEntity = chatRoomRepository.save(chatRoomEntity);

        return ChatMapper.mapChatRoomEntityToDomain(savedChatRoomEntity);
    }

    public ChatRoom createNewGroupRoom(final RoomType type, final String name, final Integer maxMemberCount) {
        final ChatRoomEntity chatRoomEntity = ChatRoomEntity.builder()
                .name(name)
                .type(type)
                .maxMemberCount(maxMemberCount)
                .build();
        final ChatRoomEntity savedChatRoomEntity = chatRoomRepository.save(chatRoomEntity);

        return ChatMapper.mapChatRoomEntityToDomain(savedChatRoomEntity);
    }

    public ChatMessage enterRoom(final Member member, final ChatRoom chatRoom) {
        final Long memberId = member.getMemberId();
        final Long chatRoomId = chatRoom.getChatRoomId();

        if (chatRoomMemberRepository.existsByActiveTrueAndChatRoomIdAndMemberId(chatRoomId, memberId)) {
            throw new ChatException(CHAT_MEMBER_IS_ALREADY_IN_ROOM, chatRoomId, memberId);
        }

        activateRoom(chatRoomId, memberId);
        chatRoom.increaseMemberCount();
        chatRoomRepository.updateMemberCount(chatRoomId, chatRoom.getMemberCount());

        return sendMessage(ENTER, MessageType.makeEnterMessage(member.getNickname()), member, chatRoom);
    }

    private void activateRoom(final Long chatRoomId, final Long memberId) {
        if (chatRoomMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, memberId)) {
            chatRoomMemberRepository.updateChatRoomMemberActiveStatus(chatRoomId, memberId, TRUE);

            return;
        }

        chatRoomMemberRepository.save(ChatRoomMemberEntity.builder()
                .memberId(memberId)
                .chatRoomId(chatRoomId)
                .build());
    }

    public ChatMessage sendMessage(
            final MessageType type,
            final String content,
            final Member sender,
            final ChatRoom chatRoom
    ) {
        final Long chatRoomId = chatRoom.getChatRoomId();
        final Long senderId = sender.getMemberId();

        if (!chatRoomMemberRepository.existsByActiveTrueAndChatRoomIdAndMemberId(chatRoomId, senderId)) {
            throw new ChatException(CHAT_MEMBER_IS_NOT_IN_ROOM, chatRoomId, senderId);
        }

        final ChatMessageEntity chatMessageEntity = ChatMessageEntity.builder()
                .type(type)
                .content(content)
                .senderId(senderId)
                .chatRoomId(chatRoomId)
                .build();
        final ChatMessageEntity savedChatMessageEntity = chatMessageRepository.save(chatMessageEntity);

        return ChatMapper.mapChatMessageEntityToDomain(savedChatMessageEntity, sender, chatRoom);
    }

    public ChatMessage leaveRoom(final Member member, final ChatRoom chatRoom) {
        final Long memberId = member.getMemberId();
        final Long chatRoomId = chatRoom.getChatRoomId();

        if (!chatRoomMemberRepository.existsByActiveTrueAndChatRoomIdAndMemberId(chatRoomId, memberId)) {
            throw new ChatException(CHAT_MEMBER_IS_NOT_IN_ROOM, chatRoomId, memberId);
        }

        validateCanLeaveChatRoom(memberId, chatRoom);

        final ChatMessage leaveMessage = sendMessage(
                LEAVE,
                MessageType.makeLeaveMessage(member.getNickname()),
                member,
                chatRoom
        );

        chatRoomMemberRepository.updateChatRoomMemberActiveStatus(chatRoomId, memberId, FALSE);
        chatRoom.decreaseMemberCount();
        chatRoomRepository.updateMemberCount(chatRoomId, chatRoom.getMemberCount());

        if (chatRoom.isEmpty()) {
            chatRoomRepository.deleteById(chatRoomId);
        }

        return leaveMessage;
    }

    private void validateCanLeaveChatRoom(final Long memberId, final ChatRoom chatRoom) {
        if (chatRoom.getType() == CREW) {
            validateCanLeaveCrewChatRoom(memberId, chatRoom);
        }

        if (chatRoom.getType() == GAME) {
            validateCanLeaveGameChatRoom(chatRoom);
        }
    }

    private void validateCanLeaveCrewChatRoom(final Long memberId, final ChatRoom chatRoom) {
        final Optional<CrewEntity> crewEntity = crewRepository.findByChatRoomId(chatRoom.getChatRoomId());

        if (crewEntity.isPresent() && existsMemberInCrew(crewEntity.get().getId(), memberId)) {
            throw new ChatException(CHAT_CREW_CHATROOM_NOT_ALLOWED_TO_LEAVE);
        }
    }

    private Boolean existsMemberInCrew(final Long crewId, final Long memberId) {
        return crewMemberRepository.existsByCrewIdAndMemberIdAndStatus(crewId, memberId, CONFIRMED);
    }

    private void validateCanLeaveGameChatRoom(final ChatRoom chatRoom) {
        final Optional<GameEntity> gameEntity = gameRepository.findByChatRoomId(chatRoom.getChatRoomId());

        if (gameEntity.isPresent() && isGameNotEnded(gameEntity.get().getPlayEndDatetime())) {
            throw new ChatException(CHAT_GAME_CHATROOM_NOT_ALLOWED_TO_LEAVE);
        }
    }

    private Boolean isGameNotEnded(final LocalDateTime gameEndDatetime) {
        return DateTimeUtil.isAfterThanNow(gameEndDatetime);
    }
}
