package kr.pickple.back.chat.service;

import static kr.pickple.back.chat.domain.MessageType.*;
import static kr.pickple.back.chat.exception.ChatExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.MessageType;
import kr.pickple.back.chat.dto.request.ChatMessageCreateRequest;
import kr.pickple.back.chat.dto.response.ChatMessageResponse;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.repository.ChatMessageRepository;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.common.util.DateTimeUtil;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final ChatValidator chatValidator;

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ChatMessageResponse enterChatRoom(
            final Long roomId,
            final ChatMessageCreateRequest chatMessageCreateRequest
    ) {
        final Member member = findMemberById(chatMessageCreateRequest.getSenderId());
        final ChatRoom chatRoom = findRoomById(roomId);
        final ChatMessage enteringMessage = enterRoomAndSaveEnteringMessages(chatRoom, member);

        return ChatMessageResponse.from(enteringMessage);
    }

    @Transactional
    public ChatMessage enterRoomAndSaveEnteringMessages(final ChatRoom chatRoom, final Member member) {
        chatValidator.validateIsNotExistedRoomMember(chatRoom, member);

        final String content = MessageType.makeEnterMessage(member.getNickname());
        final ChatMessage chatMessage = buildChatMessage(ENTER, content, chatRoom, member);
        chatRoom.enterRoom(chatMessage);

        return chatMessageRepository.save(chatMessage);
    }

    @Transactional
    public ChatMessageResponse sendMessage(
            final Long roomId,
            final ChatMessageCreateRequest chatMessageCreateRequest
    ) {
        final Member sender = findMemberById(chatMessageCreateRequest.getSenderId());
        final ChatRoom chatRoom = findRoomById(roomId);

        chatValidator.validateIsExistedRoomMember(sender, chatRoom);

        final String content = chatMessageCreateRequest.getContent();
        final ChatMessage chatMessage = buildChatMessage(TALK, content, chatRoom, sender);
        chatRoom.sendMessage(chatMessage);
        final ChatMessage sendingMessage = chatMessageRepository.save(chatMessage);

        return ChatMessageResponse.from(sendingMessage);
    }

    @Transactional
    public ChatMessageResponse leaveChatRoom(
            final Long roomId,
            final ChatMessageCreateRequest chatMessageCreateRequest
    ) {
        final Member member = findMemberById(chatMessageCreateRequest.getSenderId());
        final ChatRoom chatRoom = findRoomById(roomId);

        chatValidator.validateIsExistedRoomMember(member, chatRoom);
        chatValidator.validateChatRoomLeavingConditions(member, chatRoom);

        final String content = MessageType.makeLeaveMessage(member.getNickname());
        final ChatMessage chatMessage = buildChatMessage(LEAVE, content, chatRoom, member);
        chatRoom.leaveRoom(chatMessage);
        final ChatMessage leavingMessage = chatMessageRepository.save(chatMessage);

        if (chatRoom.isEmptyRoom()) {
            chatRoomRepository.delete(chatRoom);
        }

        return ChatMessageResponse.from(leavingMessage);
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
                .chatRoom(chatRoom)
                .sender(member)
                .build();
    }

    public List<ChatMessageResponse> findAllMessagesInRoom(final Long loggedInMemberId, final Long roomId) {
        final ChatRoom chatRoom = findRoomById(roomId);
        final Member loggedInMember = findMemberById(loggedInMemberId);

        chatValidator.validateIsExistedRoomMember(loggedInMember, chatRoom);

        final ChatMessage lastEnteringMessage = chatRoom.getLastEnteringChatMessageByMember(loggedInMember);

        return chatRoom.getChatMessages()
                .stream()
                .filter(thisChatMessage -> isEqualOrAfterThanLastEnteringMessage(lastEnteringMessage, thisChatMessage))
                .map(ChatMessageResponse::from)
                .toList();
    }

    private Boolean isEqualOrAfterThanLastEnteringMessage(
            final ChatMessage lastEnteringMessage,
            final ChatMessage thisChatMessage
    ) {
        return DateTimeUtil.isEqualOrAfter(lastEnteringMessage.getCreatedAt(), thisChatMessage.getCreatedAt());
    }

    private ChatRoom findRoomById(final Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND, roomId));
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }
}
