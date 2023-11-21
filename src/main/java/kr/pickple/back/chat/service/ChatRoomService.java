package kr.pickple.back.chat.service;

import static java.text.MessageFormat.*;
import static kr.pickple.back.chat.domain.RoomType.*;
import static kr.pickple.back.chat.exception.ChatExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.RoomType;
import kr.pickple.back.chat.dto.request.PersonalChatRoomCreateRequest;
import kr.pickple.back.chat.dto.response.ChatRoomDetailResponse;
import kr.pickple.back.chat.dto.response.ChatRoomExistedResponse;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.repository.ChatRoomMemberRepository;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatMessageService chatMessageService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ChatRoomDetailResponse createPersonalRoom(
            final Long senderId,
            final PersonalChatRoomCreateRequest personalChatRoomCreateRequest
    ) {
        final Long receiverId = personalChatRoomCreateRequest.getReceiverId();
        final Member receiver = findMemberById(receiverId);
        final Member sender = findMemberById(senderId);

        validateIsSelfChat(receiver, sender);

        final String personalRoomName = format("{0},{1}", sender.getNickname(), receiver.getNickname());
        final ChatRoom savedChatRoom = saveNewChatRoom(sender, personalRoomName, PERSONAL);
        chatMessageService.enterRoomAndSaveEnteringMessages(savedChatRoom, receiver);

        return ChatRoomDetailResponse.of(savedChatRoom, receiver);
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

    public ChatRoomExistedResponse existsChatRoomWithReceiver(final Long senderId, final Long receiverId) {
        final Member sender = findMemberById(senderId);
        final Member receiver = findMemberById(receiverId);

        validateIsSelfChat(receiver, sender);

        final Boolean isExisted = chatRoomMemberRepository.findAllByMember(sender)
                .stream()
                .anyMatch(chatRoomMember -> isPersonalChatRoomWithReceiver(receiver, chatRoomMember.getChatRoom()));

        return ChatRoomExistedResponse.from(isExisted);
    }

    private void validateIsSelfChat(Member receiver, Member sender) {
        if (sender.equals(receiver)) {
            throw new ChatException(CHAT_MEMBER_CANNOT_CHAT_SELF, sender.getId());
        }
    }

    private boolean isPersonalChatRoomWithReceiver(final Member receiver, final ChatRoom chatRoom) {
        return chatRoom.isMatchedRoomType(PERSONAL) && chatRoom.isEntered(receiver);
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }
}
