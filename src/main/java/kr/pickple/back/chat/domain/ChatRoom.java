package kr.pickple.back.chat.domain;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.util.RoomTypeAttributeConverter;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @NotNull
    @Column(length = 20)
    private String name;

    @Getter
    @NotNull
    @Convert(converter = RoomTypeAttributeConverter.class)
    private RoomType type;

    @Getter
    @NotNull
    private Integer memberCount = 0;

    @Getter
    @NotNull
    private Integer maxMemberCount = 2;

    @Embedded
    private ChatRoomMembers chatRoomMembers = new ChatRoomMembers();

    @Embedded
    private ChatMessages chatMessages = new ChatMessages();

    @Builder
    private ChatRoom(final String name, final RoomType type) {
        this.name = name;
        this.type = type;
    }

    public void increaseMemberCount() {
        if (isFullRoom()) {
            throw new ChatException(CHAT_ROOM_IS_FULL, memberCount);
        }

        memberCount += 1;
    }

    private Boolean isFullRoom() {
        return memberCount.equals(maxMemberCount);
    }

    public void decreaseMemberCount() {
        if (isEmptyRoom()) {
            throw new ChatException(CHAT_ROOM_IS_EMPTY, memberCount);
        }

        memberCount -= 1;
    }

    public Boolean isEmptyRoom() {
        return memberCount == 0;
    }

    public Boolean isEntered(final Member member) {
        return chatRoomMembers.isMemberEnteredRoom(member);
    }

    public Boolean isMatchedRoomType(final RoomType type) {
        return this.type == type;
    }

    public void updateMaxMemberCount(final Integer maxMemberCount) {
        if (maxMemberCount < memberCount) {
            throw new ChatException(CHAT_MAX_MEMBER_COUNT_SHOULD_BE_BIGGER_THAN_MEMBER_COUNT, maxMemberCount);
        }

        this.maxMemberCount = maxMemberCount;
    }

    public void sendMessage(final ChatMessage chatMessage) {
        chatMessages.addChatMessage(chatMessage);
    }

    public void enterRoom(final ChatMessage chatMessage) {
        final Member newMember = chatMessage.getSender();
        chatRoomMembers.activateChatRoomMember(this, newMember);

        sendMessage(chatMessage);
        increaseMemberCount();
    }

    public void leaveRoom(final ChatMessage chatMessage) {
        final Member member = chatMessage.getSender();
        chatRoomMembers.deactivateChatRoomMember(this, member);

        sendMessage(chatMessage);
        decreaseMemberCount();
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages.getChatMessages();
    }

    public List<Member> getActiveMembersInRoom() {
        return chatRoomMembers.getActiveMembers();
    }

    public List<Member> getAllMembersInRoom() {
        return chatRoomMembers.getAllMembers();
    }

    public ChatMessage getLastChatMessage() {
        return chatMessages.getLastChatMessage();
    }

    public ChatMessage getLastEnteringChatMessageByMember(final Member member) {
        return chatMessages.getLastEnteringChatMessageByMember(member);
    }
}
