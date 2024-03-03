package kr.pickple.back.chat.service;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatRoomDomain;
import kr.pickple.back.chat.domain.PersonalChatRoomStatus;
import kr.pickple.back.chat.dto.mapper.ChatResponseMapper;
import kr.pickple.back.chat.dto.response.ChatRoomDetailResponse;
import kr.pickple.back.chat.dto.response.PersonalChatRoomStatusResponse;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.implement.ChatReader;
import kr.pickple.back.chat.implement.ChatWriter;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final MemberReader memberReader;
    private final ChatReader chatReader;
    private final ChatWriter chatWriter;

    /**
     * 새 1:1 채팅방 생성
     */
    @Transactional
    public ChatRoomDetailResponse createPersonalRoom(final Long senderId, final Long receiverId) {
        validateSelfChat(senderId, receiverId);

        final MemberDomain sender = memberReader.readByMemberId(senderId);
        final MemberDomain receiver = memberReader.readByMemberId(receiverId);

        final String roomName = MessageFormat.format("{0},{1}", sender.getNickname(), receiver.getNickname());
        final ChatRoomDomain chatRoom = chatWriter.createNewPersonalRoom(roomName);

        chatWriter.enterRoom(sender, chatRoom);
        chatWriter.enterRoom(receiver, chatRoom);

        return ChatResponseMapper.mapToPersonalChatRoomDetailResponseDto(sender, receiver, chatRoom);
    }

    /**
     * 특정 사용자와의 1:1 채팅방 존재 여부 조회
     */
    public PersonalChatRoomStatusResponse findPersonalChatRoomStatus(
            final Long senderId,
            final Long receiverId
    ) {
        validateSelfChat(senderId, receiverId);

        final PersonalChatRoomStatus personalChatRoomStatus = chatReader.readPersonalRoomStatus(senderId, receiverId);

        return ChatResponseMapper.mapToPersonalChatRoomStatusResponseDto(personalChatRoomStatus);
    }

    private void validateSelfChat(final Long senderId, final Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new ChatException(CHAT_MEMBER_CANNOT_CHAT_SELF, senderId);
        }
    }
}
