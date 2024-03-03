package kr.pickple.back.chat.service;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;
import static kr.pickple.back.common.domain.RegistrationStatus.*;

import java.util.Optional;

import org.springframework.stereotype.Component;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.repository.ChatRoomMemberRepository;
import kr.pickple.back.common.util.DateTimeUtil;
import kr.pickple.back.crew.repository.CrewMemberRepository;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.crew.repository.entity.CrewEntity;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatValidator {

    private final CrewRepository crewRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final GameRepository gameRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public void validateIsExistedRoomMember(final Member member, final ChatRoom chatRoom) {
        if (!isExistedRoomMember(chatRoom, member)) {
            throw new ChatException(CHAT_MEMBER_IS_NOT_IN_ROOM, member.getId(), chatRoom.getId());
        }
    }

    private Boolean isExistedRoomMember(final ChatRoom chatRoom, final Member member) {
        return chatRoomMemberRepository.existsByActiveTrueAndChatRoomIdAndMemberId(chatRoom.getId(), member.getId());
    }

    public void validateChatRoomLeavingConditions(final Member member, final ChatRoom chatRoom) {
        switch (chatRoom.getType()) {
            case CREW -> validateCrewChatRoomLeavingConditions(member, chatRoom);
            case GAME -> validateGameChatRoomLeavingConditions(member, chatRoom);
        }
    }

    private void validateCrewChatRoomLeavingConditions(final Member member, final ChatRoom chatRoom) {
        final Optional<CrewEntity> optionalCrew = crewRepository.findByChatRoomId(chatRoom.getId());

        if (optionalCrew.isEmpty()) {
            return;
        }

        final CrewEntity crew = optionalCrew.get();
        validateIsMemberConfirmedCrewMember(crew.getId(), member.getId(), chatRoom.getId());
    }

    private void validateIsMemberConfirmedCrewMember(final Long crewId, final Long memberId, final Long chatRoomId) {
        if (crewMemberRepository.existsByCrewIdAndMemberIdAndStatus(crewId, memberId, CONFIRMED)) {
            throw new ChatException(CHAT_CREW_CHATROOM_NOT_ALLOWED_TO_LEAVE, crewId, memberId, chatRoomId);
        }
    }

    private void validateGameChatRoomLeavingConditions(final Member member, final ChatRoom chatRoom) {
        final Optional<Game> optionalGame = gameRepository.findByChatRoomId(chatRoom.getId());

        if (optionalGame.isEmpty()) {
            return;
        }

        final Game game = optionalGame.get();

        if (isGameNotOver(game)) {
            throw new ChatException(CHAT_GAME_CHATROOM_NOT_ALLOWED_TO_LEAVE, member.getId(), game.getId());
        }
    }

    private Boolean isGameNotOver(final Game game) {
        return DateTimeUtil.isAfterThanNow(game.getPlayEndDatetime());
    }
}
