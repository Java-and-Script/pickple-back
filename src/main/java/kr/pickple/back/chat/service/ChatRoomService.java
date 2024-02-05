package kr.pickple.back.chat.service;

import static java.text.MessageFormat.*;
import static kr.pickple.back.chat.domain.RoomType.*;
import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.ChatRoomMember;
import kr.pickple.back.chat.domain.RoomType;
import kr.pickple.back.chat.dto.request.PersonalChatRoomCreateRequest;
import kr.pickple.back.chat.dto.response.ChatMemberResponse;
import kr.pickple.back.chat.dto.response.ChatRoomDetailResponse;
import kr.pickple.back.chat.dto.response.PersonalChatRoomExistedResponse;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.repository.ChatRoomMemberRepository;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageService chatMessageService;
    private final ChatValidator chatValidator;

    /**
     * 새 1:1 채팅방 생성
     */
    @Transactional
    public ChatRoomDetailResponse createPersonalRoom(
            final Long senderId,
            final PersonalChatRoomCreateRequest personalChatRoomCreateRequest
    ) {
        final Long receiverId = personalChatRoomCreateRequest.getReceiverId();
        final Member receiver = memberRepository.getMemberById(receiverId);
        final Member sender = memberRepository.getMemberById(senderId);

        chatValidator.validateIsSelfChat(receiver, sender);

        final String personalRoomName = format("{0},{1}", sender.getNickname(), receiver.getNickname());
        final ChatRoom savedChatRoom = saveNewChatRoom(sender, personalRoomName, PERSONAL);
        chatMessageService.enterRoomAndSaveEnteringMessages(savedChatRoom, receiver);

        return ChatRoomDetailResponse.of(savedChatRoom, receiver, getChatMemberResponses(savedChatRoom));
    }

    private List<ChatMemberResponse> getChatMemberResponses(final ChatRoom chatRoom) {
        return chatRoomMemberRepository.findAllByActiveTrueAndChatRoomId(chatRoom.getId())
                .stream()
                .map(ChatRoomMember::getMember)
                .map(ChatMemberResponse::from)
                .toList();
    }

    @Transactional
    public ChatRoom saveNewChatRoom(final Member member, final String roomName, final RoomType type) {
        final ChatRoom newChatRoom = ChatRoom.builder()
                .type(type)
                .name(roomName)
                .build();

        final ChatRoom savedChatRoom = chatRoomRepository.save(newChatRoom);
        chatMessageService.enterRoomAndSaveEnteringMessages(savedChatRoom, member);

        return savedChatRoom;
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
                .filter(chatRoomMember -> existsReceiverInPersonalChatRoom(receiver, chatRoomMember.getChatRoom()))
                .findFirst()
                .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND));

        final Long personalChatRoomId = foundChatRoomMember.getChatRoom().getId();
        final Boolean isSenderActive = foundChatRoomMember.isActive();

        return PersonalChatRoomExistedResponse.of(personalChatRoomId, isSenderActive);
    }

    private Boolean existsReceiverInPersonalChatRoom(final Member receiver, final ChatRoom chatRoom) {
        return chatRoom.isMatchedRoomType(PERSONAL)
                && chatRoomMemberRepository.existsByChatRoomIdAndMemberId(chatRoom.getId(), receiver.getId());
    }
}
