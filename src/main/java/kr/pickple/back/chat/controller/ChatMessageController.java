package kr.pickple.back.chat.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import kr.pickple.back.auth.config.resolver.Login;
import kr.pickple.back.chat.dto.request.ChatMessageCreateRequest;
import kr.pickple.back.chat.dto.response.ChatMessageResponse;
import kr.pickple.back.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/messages/enter/{roomId}")
    @SendTo("/receive/rooms/{roomId}")
    public ChatMessageResponse enterChatRoom(
            @DestinationVariable final Long roomId,
            @Payload final ChatMessageCreateRequest chatMessageCreateRequest
    ) {
        return chatMessageService.enterChatRoom(roomId, chatMessageCreateRequest);
    }

    @MessageMapping("/messages/talk/{roomId}")
    @SendTo("/receive/rooms/{roomId}")
    public ChatMessageResponse sendMessage(
            @DestinationVariable final Long roomId,
            @Payload final ChatMessageCreateRequest chatMessageCreateRequest
    ) {
        return chatMessageService.sendMessage(roomId, chatMessageCreateRequest);
    }

    @MessageMapping("/messages/leave/{roomId}")
    @SendTo("/receive/rooms/{roomId}")
    public ChatMessageResponse leaveChatRoom(
            @DestinationVariable final Long roomId,
            @Payload final ChatMessageCreateRequest chatMessageCreateRequest
    ) {
        return chatMessageService.leaveChatRoom(roomId, chatMessageCreateRequest);
    }

    @GetMapping("/messages/rooms/{roomId}")
    public ResponseEntity<List<ChatMessageResponse>> findAllMessagesInRoom(
            @Login final Long loggedInMemberId,
            @PathVariable final Long roomId
    ) {
        return ResponseEntity.status(OK)
                .body(chatMessageService.findAllMessagesInRoom(loggedInMemberId, roomId));
    }
}
