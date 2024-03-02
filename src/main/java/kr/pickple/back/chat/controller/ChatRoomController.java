package kr.pickple.back.chat.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.pickple.back.auth.config.resolver.Login;
import kr.pickple.back.chat.domain.RoomType;
import kr.pickple.back.chat.dto.request.PersonalChatRoomCreateRequest;
import kr.pickple.back.chat.dto.response.ChatRoomDetailResponse;
import kr.pickple.back.chat.dto.response.ChatRoomResponse;
import kr.pickple.back.chat.dto.response.PersonalChatRoomExistedResponse;
import kr.pickple.back.chat.service.ChatRoomFindService;
import kr.pickple.back.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomFindService chatRoomFindService;

    @PostMapping("/personal")
    public ResponseEntity<ChatRoomDetailResponse> createPersonalRoom(
            @Login final Long senderId,
            @RequestBody final PersonalChatRoomCreateRequest personalChatRoomCreateRequest
    ) {
        return ResponseEntity.status(CREATED)
                .body(chatRoomService.createPersonalRoom(senderId, personalChatRoomCreateRequest.getReceiverId()));
    }

    @GetMapping("/personal")
    public ResponseEntity<PersonalChatRoomExistedResponse> findActivePersonalChatRoomWithReceiver(
            @Login final Long senderId,
            @RequestParam("receiver") final Long receiverId
    ) {
        return ResponseEntity.status(OK)
                .body(chatRoomService.findActivePersonalChatRoomWithReceiver(senderId, receiverId));
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> findAllActiveChatRoomsByType(
            @Login final Long loggedInMemberId,
            @RequestParam final RoomType type
    ) {
        return ResponseEntity.status(OK)
                .body(chatRoomFindService.findAllActiveChatRoomsByType(loggedInMemberId, type));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomDetailResponse> findChatRoomById(
            @Login final Long loggedInMemberId,
            @PathVariable final Long roomId
    ) {
        return ResponseEntity.status(OK)
                .body(chatRoomFindService.findChatRoomById(loggedInMemberId, roomId));
    }
}
