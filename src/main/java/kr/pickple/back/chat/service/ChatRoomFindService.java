package kr.pickple.back.chat.service;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatMessage;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.RoomType;
import kr.pickple.back.chat.dto.response.ChatMemberResponse;
import kr.pickple.back.chat.dto.response.ChatRoomDetailResponse;
import kr.pickple.back.chat.dto.response.ChatRoomResponse;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.repository.ChatMessageRepository;
import kr.pickple.back.chat.repository.ChatRoomMemberRepository;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomFindService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final CrewRepository crewRepository;
    private final GameRepository gameRepository;

    /**
     * 채팅방 타입에 따른 참여중인 모든 채팅방 목록 조회
     */
    public List<ChatRoomResponse> findAllActiveChatRoomsByType(final Long loggedInMemberId, final RoomType type) {
        final Member loggedInMember = memberRepository.getMemberById(loggedInMemberId);

        return chatRoomMemberRepository.findAllByActiveTrueAndMemberId(loggedInMember.getId())
                .stream()
                .map(chatRoomMember -> chatRoomRepository.getChatRoomById(chatRoomMember.getChatRoomId()))
                .filter(chatRoom -> chatRoom.isMatchedRoomType(type))
                .map(chatRoom -> getChatRoomResponse(loggedInMemberId, chatRoom))
                .toList();
    }

    private ChatRoomResponse getChatRoomResponse(final Long loggedInMemberId, final ChatRoom chatRoom) {
        final ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(chatRoom.getId());
        final ChatRoomDetailResponse chatRoomDetail = getChatRoomDetailResponse(loggedInMemberId, chatRoom);

        return ChatRoomResponse.of(chatRoomDetail, lastMessage.getContent(), lastMessage.getCreatedAt());
    }

    /**
     * 단일 채팅방 정보 상세 조회
     */
    public ChatRoomDetailResponse findChatRoomById(final Long loggedInMemberId, final Long roomId) {
        final ChatRoom chatRoom = chatRoomRepository.getChatRoomById(roomId);

        return getChatRoomDetailResponse(loggedInMemberId, chatRoom);
    }

    private ChatRoomDetailResponse getChatRoomDetailResponse(final Long loggedInMemberId, final ChatRoom chatRoom) {
        return switch (chatRoom.getType()) {
            case PERSONAL -> getPersonalChatRoomDetailResponse(loggedInMemberId, chatRoom);
            case CREW -> getCrewChatRoomDetailResponse(chatRoom);
            case GAME -> getGameChatRoomDetailResponse(chatRoom);
        };
    }

    private ChatRoomDetailResponse getPersonalChatRoomDetailResponse(final Long memberId, final ChatRoom chatRoom) {
        final Member sender = memberRepository.getMemberById(memberId);
        final Long receiverId = chatRoomMemberRepository.getPersonalChatRoomReceiver(chatRoom.getId(), sender.getId())
                .getMemberId();
        final Member receiver = memberRepository.getMemberById(receiverId);

        return ChatRoomDetailResponse.of(chatRoom, receiver, getChatMemberResponses(chatRoom));
    }

    private ChatRoomDetailResponse getCrewChatRoomDetailResponse(final ChatRoom chatRoom) {
        final CrewEntity crew = crewRepository.findByChatRoomId(chatRoom.getId())
                .orElseThrow(() -> new ChatException(CHAT_CREW_NOT_FOUND, chatRoom.getId()));

        return ChatRoomDetailResponse.of(chatRoom, crew, getChatMemberResponses(chatRoom));
    }

    private ChatRoomDetailResponse getGameChatRoomDetailResponse(final ChatRoom chatRoom) {
        final Game game = gameRepository.findByChatRoomId(chatRoom.getId())
                .orElseThrow(() -> new ChatException(CHAT_GAME_NOT_FOUND, chatRoom.getId()));

        return ChatRoomDetailResponse.of(chatRoom, game, getChatMemberResponses(chatRoom));
    }

    private List<ChatMemberResponse> getChatMemberResponses(final ChatRoom chatRoom) {
        return chatRoomMemberRepository.findAllByActiveTrueAndChatRoomId(chatRoom.getId())
                .stream()
                .map(chatRoomMember -> memberRepository.getMemberById(chatRoomMember.getMemberId()))
                .map(ChatMemberResponse::from)
                .toList();
    }
}
