package kr.pickple.back.chat.service;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.RoomType;
import kr.pickple.back.chat.dto.mapper.ChatResponseMapper;
import kr.pickple.back.chat.dto.response.ChatRoomDetailResponse;
import kr.pickple.back.chat.dto.response.ChatRoomResponse;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.implement.ChatReader;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.implement.CrewReader;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.implement.MemberReader;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomFindService {

    private final MemberReader memberReader;
    private final CrewReader crewReader;
    private final ChatReader chatReader;
    private final GameRepository gameRepository;

    /**
     * 채팅방 타입에 따른 참여중인 모든 채팅방 목록 조회
     */
    public List<ChatRoomResponse> findAllEnteringChatRoomsByType(final Long loggedInMemberId, final RoomType type) {
        if (!memberReader.existsByMemberId(loggedInMemberId)) {
            throw new MemberException(MEMBER_NOT_FOUND, loggedInMemberId);
        }

        return chatReader.readEnteringRoomsByType(loggedInMemberId, type)
                .stream()
                .map(chatRoom -> ChatResponseMapper.mapToChatRoomResponseDto(
                        chatReader.readLastMessage(chatRoom),
                        getChatRoomDetailResponse(loggedInMemberId, chatRoom))
                ).toList();
    }

    /**
     * 단일 채팅방 정보 상세 조회
     */
    public ChatRoomDetailResponse findChatRoomById(final Long loggedInMemberId, final Long chatRoomId) {
        final ChatRoom chatRoom = chatReader.readRoom(chatRoomId);

        return getChatRoomDetailResponse(loggedInMemberId, chatRoom);
    }

    private ChatRoomDetailResponse getChatRoomDetailResponse(final Long memberId, final ChatRoom chatRoom) {
        return switch (chatRoom.getType()) {
            case PERSONAL -> getPersonalChatRoomDetailResponse(memberId, chatRoom);
            case CREW -> getCrewChatRoomDetailResponse(chatRoom);
            case GAME -> getGameChatRoomDetailResponse(chatRoom);
        };
    }

    private ChatRoomDetailResponse getPersonalChatRoomDetailResponse(
            final Long senderId,
            final ChatRoom chatRoom
    ) {
        final MemberDomain sender = memberReader.readByMemberId(senderId);
        final MemberDomain receiver = chatReader.readReceiver(senderId, chatRoom.getChatRoomId());

        return ChatResponseMapper.mapToPersonalChatRoomDetailResponseDto(sender, receiver, chatRoom);
    }

    private ChatRoomDetailResponse getCrewChatRoomDetailResponse(final ChatRoom chatRoom) {
        final Crew crew = crewReader.readByChatRoomId(chatRoom.getChatRoomId());
        final List<MemberDomain> members = chatReader.readRoomMembers(chatRoom.getChatRoomId());

        return ChatResponseMapper.mapToCrewChatRoomDetailResponseDto(crew, chatRoom, members);
    }

    private ChatRoomDetailResponse getGameChatRoomDetailResponse(final ChatRoom chatRoom) {
        final Game game = gameRepository.findByChatRoomId(chatRoom.getChatRoomId())
                .orElseThrow(() -> new ChatException(CHAT_GAME_NOT_FOUND, chatRoom.getChatRoomId()));
        final List<MemberDomain> members = chatReader.readRoomMembers(chatRoom.getChatRoomId());

        return ChatResponseMapper.mapToGameChatRoomDetailResponseDto(game, chatRoom, members);
    }
}
