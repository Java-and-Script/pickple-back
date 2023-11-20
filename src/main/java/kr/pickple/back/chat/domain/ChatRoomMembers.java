package kr.pickple.back.chat.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import kr.pickple.back.member.domain.Member;

@Embeddable
public class ChatRoomMembers {

    @OneToMany(mappedBy = "chatRoom", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<ChatRoomMember> chatRoomMembers = new ArrayList<>();

    void addChatRoomMember(final ChatRoom chatRoom, final Member member) {
        final ChatRoomMember chatRoomMember = buildChatRoom(chatRoom, member);
        chatRoomMembers.add(chatRoomMember);
    }

    void removeChatRoomMember(final ChatRoom chatRoom, final Member member) {
        final ChatRoomMember chatRoomMember = buildChatRoom(chatRoom, member);
        chatRoomMembers.remove(chatRoomMember);
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

    private ChatRoomMember buildChatRoom(final ChatRoom chatRoom, final Member member) {
        return ChatRoomMember.builder()
                .member(member)
                .chatRoom(chatRoom)
                .build();
    }
}
