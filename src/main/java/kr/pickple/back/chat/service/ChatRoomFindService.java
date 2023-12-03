package kr.pickple.back.chat.service;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.ChatRoomMember;
import kr.pickple.back.chat.domain.RoomType;
import kr.pickple.back.chat.dto.response.ChatRoomDetailResponse;
import kr.pickple.back.chat.dto.response.ChatRoomResponse;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.repository.ChatRoomMemberRepository;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomFindService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final GameRepository gameRepository;

    public List<ChatRoomResponse> findAllActiveChatRoomsByType(final Long loggedInMemberId, final RoomType type) {
        final Member loggedInMember = findMemberById(loggedInMemberId);

        return chatRoomMemberRepository.findAllByActiveTrueAndMember(loggedInMember)
                .stream()
                .map(ChatRoomMember::getChatRoom)
                .filter(chatRoom -> chatRoom.isMatchedRoomType(type))
                .map(chatRoom -> getChatRoomResponse(loggedInMemberId, chatRoom))
                .toList();
    }

    private ChatRoomResponse getChatRoomResponse(final Long loggedInMemberId, final ChatRoom chatRoom) {
        final String lastMessageContent = chatRoom.getLastChatMessage().getContent();
        final LocalDateTime lastMessageCreatedAt = chatRoom.getLastChatMessage().getCreatedAt();
        final ChatRoomDetailResponse chatRoomDetail = getChatRoomDetailResponse(loggedInMemberId, chatRoom);

        return ChatRoomResponse.of(chatRoomDetail, lastMessageContent, lastMessageCreatedAt);
    }

    public ChatRoomDetailResponse findChatRoomById(final Long loggedInMemberId, final Long roomId) {
        return getChatRoomDetailResponse(loggedInMemberId, findRoomById(roomId));
    }

    private ChatRoom findRoomById(final Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND, roomId));
    }

    private ChatRoomDetailResponse getChatRoomDetailResponse(final Long loggedInMemberId, final ChatRoom chatRoom) {
        return switch (chatRoom.getType()) {
            case PERSONAL -> getPersonalChatRoomDetailResponse(loggedInMemberId, chatRoom);
            case CREW -> getCrewChatRoomDetailResponse(chatRoom);
            case GAME -> getGameChatRoomDetailResponse(chatRoom);
        };
    }

    private ChatRoomDetailResponse getPersonalChatRoomDetailResponse(final Long memberId, final ChatRoom chatRoom) {
        final Member sender = findMemberById(memberId);

        final Member receiver = chatRoom.getAllMembersInRoom()
                .stream()
                .filter(roomMember -> !roomMember.equals(sender))
                .findFirst()
                .orElseThrow(() -> new ChatException(CHAT_RECEIVER_NOT_FOUND));

        return ChatRoomDetailResponse.of(chatRoom, receiver);
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    private ChatRoomDetailResponse getCrewChatRoomDetailResponse(final ChatRoom chatRoom) {
        final Crew crew = crewRepository.findByChatRoom(chatRoom)
                .orElseThrow(() -> new ChatException(CHAT_CREW_NOT_FOUND, chatRoom.getId()));

        return ChatRoomDetailResponse.of(chatRoom, crew);
    }

    private ChatRoomDetailResponse getGameChatRoomDetailResponse(final ChatRoom chatRoom) {
        final Game game = gameRepository.findByChatRoom(chatRoom)
                .orElseThrow(() -> new ChatException(CHAT_GAME_NOT_FOUND, chatRoom.getId()));

        return ChatRoomDetailResponse.of(chatRoom, game);
    }
}
