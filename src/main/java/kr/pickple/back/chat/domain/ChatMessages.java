package kr.pickple.back.chat.domain;

import static kr.pickple.back.chat.domain.MessageType.*;
import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.member.domain.Member;
import lombok.Getter;

@Embeddable
public class ChatMessages {

    @Getter
    @OneToMany(mappedBy = "chatRoom", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    void addChatMessage(final ChatMessage chatMessage) {
        chatMessages.add(chatMessage);
    }

    ChatMessage getLastChatMessage() {
        return chatMessages.get(chatMessages.size() - 1);
    }

    ChatMessage getLastEnteringChatMessageByMember(final Member member) {
        return chatMessages.stream()
                .filter(chatMessage -> isEnteringMessageByMember(member, chatMessage))
                .max(Comparator.comparing(ChatMessage::getCreatedAt))
                .orElseThrow(() -> new ChatException(CHAT_MEMBER_IS_NOT_IN_ROOM, member.getId()));
    }

    private Boolean isEnteringMessageByMember(final Member member, final ChatMessage chatMessage) {
        return member.equals(chatMessage.getSender()) && chatMessage.isMatchedMessageType(ENTER);
    }
}
