package kr.pickple.back.chat.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
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
}
