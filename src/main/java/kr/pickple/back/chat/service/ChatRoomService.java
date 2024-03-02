package kr.pickple.back.chat.service;

import static kr.pickple.back.chat.domain.RoomType.*;
import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import java.text.MessageFormat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.ChatRoomDomain;
import kr.pickple.back.chat.domain.ChatRoomMember;
import kr.pickple.back.chat.dto.mapper.ChatResponseMapper;
import kr.pickple.back.chat.dto.response.ChatRoomDetailResponse;
import kr.pickple.back.chat.dto.response.PersonalChatRoomExistedResponse;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.implement.ChatWriter;
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
public class ChatRoomService {

    private final MemberReader memberReader;
    private final ChatWriter chatWriter;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final ChatValidator chatValidator;

    /**
     * 새 1:1 채팅방 생성
     */
    @Transactional
    public ChatRoomDetailResponse createPersonalRoom(final Long senderId, final Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new ChatException(CHAT_MEMBER_CANNOT_CHAT_SELF, senderId);
        }

        final MemberDomain sender = memberReader.readByMemberId(senderId);
        final MemberDomain receiver = memberReader.readByMemberId(receiverId);

        final String roomName = MessageFormat.format("{0},{1}", sender.getNickname(), receiver.getNickname());
        final ChatRoomDomain chatRoom = chatWriter.createNewRoom(PERSONAL, roomName);

        chatWriter.enterRoom(sender, chatRoom);
        chatWriter.enterRoom(receiver, chatRoom);

        return ChatResponseMapper.mapToPersonalChatRoomDetailResponseDto(sender, receiver, chatRoom);
    }

    /**
     * 특정 사용자와의 1:1 채팅방 존재 여부 조회
     */
    public PersonalChatRoomExistedResponse findActivePersonalChatRoomWithReceiver(
            final Long senderId,
            final Long receiverId
    ) {
        final Member sender = memberRepository.getMemberById(senderId);
        final Member receiver = memberRepository.getMemberById(receiverId);

        chatValidator.validateIsSelfChat(receiver, sender);

        final ChatRoomMember foundChatRoomMember = chatRoomMemberRepository.findAllByMemberId(sender.getId())
                .stream()
                .filter(chatRoomMember -> existsReceiverInPersonalChatRoom(
                                receiver,
                                chatRoomRepository.getChatRoomById(chatRoomMember.getChatRoomId())
                        )
                )
                .findFirst()
                .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND));

        final Long personalChatRoomId = foundChatRoomMember.getChatRoomId();
        final Boolean isSenderActive = foundChatRoomMember.isActive();

        return PersonalChatRoomExistedResponse.of(personalChatRoomId, isSenderActive);
    }

    private Boolean existsReceiverInPersonalChatRoom(final Member receiver, final ChatRoom chatRoom) {
        return chatRoom.isMatchedRoomType(PERSONAL)
                && chatRoomMemberRepository.existsByChatRoomIdAndMemberId(chatRoom.getId(), receiver.getId());
    }
}
