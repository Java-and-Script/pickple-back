package kr.pickple.back.chat.service;

import static kr.pickple.back.chat.domain.MessageType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.dto.mapper.ChatResponseMapper;
import kr.pickple.back.chat.dto.request.ChatMessageCreateRequest;
import kr.pickple.back.chat.dto.response.ChatMessageResponse;
import kr.pickple.back.chat.implement.ChatReader;
import kr.pickple.back.chat.implement.ChatWriter;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final MemberReader memberReader;
    private final ChatReader chatReader;
    private final ChatWriter chatWriter;

    /**
     * 채팅방 입장
     */
    @Transactional
    public ChatMessageResponse enterChatRoom(
            final Long chatRoomId,
            final ChatMessageCreateRequest chatMessageCreateRequest
    ) {
        final ChatRoom chatRoom = chatReader.readRoom(chatRoomId);
        final Member newMember = memberReader.readByMemberId(chatMessageCreateRequest.getSenderId());
        final ChatMessage entranceMessage = chatWriter.enterRoom(newMember, chatRoom);

        return ChatResponseMapper.mapToChatMessageResponseDto(entranceMessage);
    }

    /**
     * 채팅 메시지 전송
     */
    @Transactional
    public ChatMessageResponse sendMessage(
            final Long chatRoomId,
            final ChatMessageCreateRequest chatMessageCreateRequest
    ) {
        final ChatRoom chatRoom = chatReader.readRoom(chatRoomId);
        final Member sender = memberReader.readByMemberId(chatMessageCreateRequest.getSenderId());
        final ChatMessage chatMessage = chatWriter.sendMessage(
                TALK,
                chatMessageCreateRequest.getContent(),
                sender,
                chatRoom
        );

        return ChatResponseMapper.mapToChatMessageResponseDto(chatMessage);
    }

    /**
     * 채팅방 퇴장
     */
    @Transactional
    public ChatMessageResponse leaveChatRoom(
            final Long chatRoomId,
            final ChatMessageCreateRequest chatMessageCreateRequest
    ) {
        final ChatRoom chatRoom = chatReader.readRoom(chatRoomId);
        final Member member = memberReader.readByMemberId(chatMessageCreateRequest.getSenderId());
        final ChatMessage leaveMessage = chatWriter.leaveRoom(member, chatRoom);

        return ChatResponseMapper.mapToChatMessageResponseDto(leaveMessage);
    }

    /**
     * 특정 채팅방의 모든 메시지 목록 조회
     */
    public List<ChatMessageResponse> findAllMessagesInRoom(final Long loggedInMemberId, final Long chatRoomId) {
        return chatReader.readMessagesAfterEntrance(loggedInMemberId, chatRoomId)
                .stream()
                .map(ChatResponseMapper::mapToChatMessageResponseDto)
                .toList();
    }
}
