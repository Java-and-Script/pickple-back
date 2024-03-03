package kr.pickple.back.chat.service;

import static kr.pickple.back.chat.domain.MessageType.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.domain.ChatMessageDomain;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.ChatRoomDomain;
import kr.pickple.back.chat.domain.ChatRoomMember;
import kr.pickple.back.chat.domain.MessageType;
import kr.pickple.back.chat.dto.mapper.ChatResponseMapper;
import kr.pickple.back.chat.dto.request.ChatMessageCreateRequest;
import kr.pickple.back.chat.dto.response.ChatMessageResponse;
import kr.pickple.back.chat.implement.ChatReader;
import kr.pickple.back.chat.implement.ChatWriter;
import kr.pickple.back.chat.repository.ChatMessageRepository;
import kr.pickple.back.chat.repository.ChatRoomMemberRepository;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.implement.MemberReader;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final MemberReader memberReader;
    private final ChatReader chatReader;
    private final ChatWriter chatWriter;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final ChatValidator chatValidator;

    /**
     * 채팅방 입장
     */
    @Transactional
    public ChatMessageResponse enterChatRoom(
            final Long chatRoomId,
            final ChatMessageCreateRequest chatMessageCreateRequest
    ) {
        final ChatRoomDomain chatRoom = chatReader.readRoom(chatRoomId);
        final MemberDomain sender = memberReader.readByMemberId(chatMessageCreateRequest.getSenderId());
        final ChatMessageDomain enteringMessage = chatWriter.enterRoom(sender, chatRoom);

        return ChatResponseMapper.mapToChatMessageResponseDto(enteringMessage);
    }

    /**
     * 채팅 메시지 전송
     */
    @Transactional
    public ChatMessageResponse sendMessage(
            final Long chatRoomId,
            final ChatMessageCreateRequest chatMessageCreateRequest
    ) {
        final ChatRoomDomain chatRoom = chatReader.readRoom(chatRoomId);
        final MemberDomain sender = memberReader.readByMemberId(chatMessageCreateRequest.getSenderId());
        final ChatMessageDomain chatMessage = chatWriter.sendMessage(
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
            final Long roomId,
            final ChatMessageCreateRequest chatMessageCreateRequest
    ) {
        final Member member = memberRepository.getMemberById(chatMessageCreateRequest.getSenderId());
        final ChatRoom chatRoom = chatRoomRepository.getChatRoomById(roomId);

        chatValidator.validateIsExistedRoomMember(member, chatRoom);
        chatValidator.validateChatRoomLeavingConditions(member, chatRoom);

        final String content = MessageType.makeLeaveMessage(member.getNickname());
        final ChatRoomMember chatRoomMember = chatRoomMemberRepository.getByMemberIdAndChatRoomId(member.getId(),
                chatRoom.getId());

        chatRoomMember.deactivate();
        chatRoom.decreaseMemberCount();

        if (chatRoom.isEmpty()) {
            chatRoomRepository.delete(chatRoom);
        }

        final ChatMessage chatMessage = buildChatMessage(LEAVE, content, chatRoom, member);
        final ChatMessage leavingMessage = chatMessageRepository.save(chatMessage);

        return ChatMessageResponse.of(leavingMessage, member, chatRoom);
    }

    private ChatMessage buildChatMessage(
            final MessageType type,
            final String content,
            final ChatRoom chatRoom,
            final Member member
    ) {
        return ChatMessage.builder()
                .type(type)
                .content(content)
                .chatRoomId(chatRoom.getId())
                .senderId(member.getId())
                .build();
    }

    /**
     * 특정 채팅방의 모든 메시지 목록 조회
     */
    public List<ChatMessageResponse> findAllMessagesInRoom(final Long loggedInMemberId, final Long roomId) {
        final ChatRoom chatRoom = chatRoomRepository.getChatRoomById(roomId);
        final Member loggedInMember = memberRepository.getMemberById(loggedInMemberId);

        chatValidator.validateIsExistedRoomMember(loggedInMember, chatRoom);

        final ChatMessage lastEnteringMessage = chatMessageRepository.getLastEnteringChatMessageBySenderId(
                loggedInMember.getId());

        final List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomIdAndCreatedAtGreaterThanEqual(
                chatRoom.getId(),
                lastEnteringMessage.getCreatedAt()
        );

        return chatMessages.stream()
                .map(chatMessage -> ChatMessageResponse.of(
                                chatMessage,
                                memberRepository.getMemberById(chatMessage.getSenderId()),
                                chatRoom
                        )
                )
                .toList();
    }
}
