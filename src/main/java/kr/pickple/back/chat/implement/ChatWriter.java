package kr.pickple.back.chat.implement;

import static java.lang.Boolean.*;
import static kr.pickple.back.chat.domain.MessageType.*;
import static kr.pickple.back.chat.domain.RoomType.*;
import static kr.pickple.back.chat.exception.ChatExceptionCode.*;
import static kr.pickple.back.common.domain.RegistrationStatus.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.domain.ChatMessageDomain;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.ChatRoomDomain;
import kr.pickple.back.chat.domain.ChatRoomMember;
import kr.pickple.back.chat.domain.MessageType;
import kr.pickple.back.chat.domain.RoomType;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.repository.ChatMessageRepository;
import kr.pickple.back.chat.repository.ChatRoomMemberRepository;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.common.util.DateTimeUtil;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.MemberDomain;
import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class ChatWriter {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final GameRepository gameRepository;
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

    public ChatMessageDomain enterRoom(final MemberDomain member, final ChatRoomDomain chatRoom) {
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

        chatRoomMemberRepository.save(ChatRoomMember.builder()
                .memberId(memberId)
                .chatRoomId(chatRoomId)
                .build());
    }

    public ChatMessageDomain sendMessage(
            final MessageType type,
            final String content,
            final MemberDomain sender,
            final ChatRoomDomain chatRoom
    ) {
        final Long chatRoomId = chatRoom.getChatRoomId();
        final Long senderId = sender.getMemberId();

        if (!chatRoomMemberRepository.existsByActiveTrueAndChatRoomIdAndMemberId(chatRoomId, senderId)) {
            throw new ChatException(CHAT_MEMBER_IS_NOT_IN_ROOM, chatRoomId, senderId);
        }

        final ChatMessage chatMessageEntity = ChatMessage.builder()
                .type(type)
                .content(content)
                .senderId(senderId)
                .chatRoomId(chatRoomId)
                .build();
        final ChatMessage savedChatMessageEntity = chatMessageRepository.save(chatMessageEntity);

        return ChatMapper.mapChatMessageEntityToDomain(savedChatMessageEntity, sender, chatRoom);
    }

    public ChatMessageDomain leaveRoom(final MemberDomain member, final ChatRoomDomain chatRoom) {
        final Long memberId = member.getMemberId();
        final Long chatRoomId = chatRoom.getChatRoomId();

        if (!chatRoomMemberRepository.existsByActiveTrueAndChatRoomIdAndMemberId(chatRoomId, memberId)) {
            throw new ChatException(CHAT_MEMBER_IS_NOT_IN_ROOM, chatRoomId, memberId);
        }

        validateCanLeaveChatRoom(memberId, chatRoom);

        final ChatMessageDomain leaveMessage = sendMessage(
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

    private void validateCanLeaveChatRoom(final Long memberId, final ChatRoomDomain chatRoom) {
        if (chatRoom.getType() == CREW) {
            validateCanLeaveCrewChatRoom(memberId, chatRoom);
        }

        if (chatRoom.getType() == GAME) {
            validateCanLeaveGameChatRoom(chatRoom);
        }
    }

    private void validateCanLeaveCrewChatRoom(final Long memberId, final ChatRoomDomain chatRoom) {
        final Optional<CrewEntity> crewEntity = crewRepository.findByChatRoomId(chatRoom.getChatRoomId());

        if (crewEntity.isPresent() && existsMemberInCrew(crewEntity.get().getId(), memberId)) {
            throw new ChatException(CHAT_CREW_CHATROOM_NOT_ALLOWED_TO_LEAVE);
        }
    }

    private Boolean existsMemberInCrew(final Long crewId, final Long memberId) {
        return crewMemberRepository.existsByCrewIdAndMemberIdAndStatus(crewId, memberId, CONFIRMED);
    }

    private void validateCanLeaveGameChatRoom(final ChatRoomDomain chatRoom) {
        final Optional<Game> gameEntity = gameRepository.findByChatRoomId(chatRoom.getChatRoomId());

        if (gameEntity.isPresent() && isGameNotEnded(gameEntity.get().getPlayEndDatetime())) {
            throw new ChatException(CHAT_GAME_CHATROOM_NOT_ALLOWED_TO_LEAVE);
        }
    }

    private Boolean isGameNotEnded(final LocalDateTime gameEndDatetime) {
        return DateTimeUtil.isAfterThanNow(gameEndDatetime);
    }
}
