package kr.pickple.back.chat.domain;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.member.domain.Member;

@Embeddable
public class ChatRoomMembers {

    @OneToMany(mappedBy = "chatRoom", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

    void activateChatRoomMember(final ChatRoom chatRoom, final Member member) {
        final ChatRoomMember chatRoomMember = buildChatRoom(chatRoom, member);

        if (chatRoomMembers.contains(chatRoomMember)) {
            final ChatRoomMember foundChatRoomMember = findChatRoomMember(chatRoom, member);
            foundChatRoomMember.activate();

            return;
        }

        chatRoomMembers.add(chatRoomMember);
    }

    void deactivateChatRoomMember(final ChatRoom chatRoom, final Member member) {
        final ChatRoomMember foundChatRoomMember = findChatRoomMember(chatRoom, member);
        foundChatRoomMember.deactivate();
    }

    private ChatRoomMember findChatRoomMember(final ChatRoom chatRoom, final Member member) {
        final ChatRoomMember chatRoomMember = buildChatRoom(chatRoom, member);

        return chatRoomMembers.stream()
                .filter(chatRoomMember::equals)
                .findFirst()
                .orElseThrow(() -> new ChatException(CHAT_MEMBER_IS_NOT_IN_ROOM, member.getId(), chatRoom.getId()));
    }

    private ChatRoomMember buildChatRoom(final ChatRoom chatRoom, final Member member) {
        return ChatRoomMember.builder()
                .member(member)
                .chatRoom(chatRoom)
                .build();
    }

    Boolean isMemberEnteredRoom(final Member member) {
        return chatRoomMembers.stream()
                .anyMatch(chatRoomMember -> member.equals(chatRoomMember.getMember()));
    }

    List<Member> getMembers() {
        return chatRoomMembers.stream()
                .map(ChatRoomMember::getMember)
                .toList();
    }
}
