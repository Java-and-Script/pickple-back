package kr.pickple.back.game.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.alarm.event.game.GameJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.game.GameMemberJoinedEvent;
import kr.pickple.back.alarm.event.game.GameMemberRejectedEvent;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.chat.repository.entity.ChatRoomEntity;
import kr.pickple.back.chat.service.ChatMessageService;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.GameDomain;
import kr.pickple.back.game.domain.GameMemberDomain;
import kr.pickple.back.game.dto.mapper.GameResponseMapper;
import kr.pickple.back.game.dto.request.GameMemberRegistrationStatusUpdateRequest;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.implement.GameMemberReader;
import kr.pickple.back.game.implement.GameMemberWriter;
import kr.pickple.back.game.implement.GameReader;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GamePositionRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.implement.MemberReader;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameMemberService {

    private final AddressReader addressReader;

    private final GameRepository gameRepository;
    private final MemberRepository memberRepository;
    private final MemberPositionRepository memberPositionRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageService chatMessageService;
    private final ApplicationEventPublisher eventPublisher;
    private final GameMemberRepository gameMemberRepository;
    private final GamePositionRepository gamePositionRepository;
    private final GameReader gameReader;
    private final MemberReader memberReader;
    private final GameMemberWriter gameMemberWriter;
    private final GameMemberReader gameMemberReader;

    @Transactional
    public void registerGameMember(final Long gameId, final Long loggedInMemberId) {
        final GameDomain gameDomain = gameReader.read(gameId);
        final MemberDomain memberDomain = memberReader.readByMemberId(loggedInMemberId);

        gameMemberWriter.register(memberDomain, gameDomain);

        eventPublisher.publishEvent(GameJoinRequestNotificationEvent.builder()
                .gameId(gameId)
                .memberId(loggedInMemberId)
                .build());
    }

    public GameResponse findAllGameMembers(
            final Long loggedInMemberId,
            final Long gameId,
            final RegistrationStatus status
    ) {
        final GameMemberDomain gameMember = gameMemberReader.readGameMemberByMemberIdAndGameId(loggedInMemberId, gameId);
        final GameDomain game = gameReader.read(gameId);
        final MemberDomain member = memberReader.readByMemberId(loggedInMemberId);

        if (!game.isHost(member.getMemberId()) && status == WAITING) {
            throw new GameException(GAME_MEMBER_IS_NOT_HOST, loggedInMemberId);
        }

        final List<MemberDomain> members = gameReader.readAllMembersByGameIdAndStatus(gameId, status);

        return GameResponseMapper.mapToGameResponseDto(game, members);
    }

    @Transactional
    public void updateGameMemberRegistrationStatus(
            final Long loggedInMemberId,
            final Long gameId,
            final Long memberId,
            final GameMemberRegistrationStatusUpdateRequest gameMemberRegistrationStatusUpdateRequest
    ) {
        final GameMemberDomain gameMember = gameMemberReader.readGameMemberByMemberIdAndGameId(loggedInMemberId, gameId);
        final GameDomain game = gameReader.read(gameId);

        validateIsHost(loggedInMemberId, game);
        final RegistrationStatus updateStatus = gameMemberRegistrationStatusUpdateRequest.getStatus();

        final ChatRoomEntity chatRoom = chatRoomRepository.getChatRoomById(gameEntity.getChatRoomId());
        enterGameChatRoom(updateStatus, gameMemberEntity, chatRoom);

        gameMemberWriter.updateMemberRegistrationStatus(gameMember, updateStatus);

        eventPublisher.publishEvent(GameMemberJoinedEvent.builder()
                .gameId(gameId)
                .memberId(memberId)
                .build());
    }

    private void validateIsHost(final Long loggedInMemberId, final GameDomain gameDomain) {
        if (!gameDomain.isHost(loggedInMemberId)) {
            throw new GameException(GAME_MEMBER_IS_NOT_HOST, loggedInMemberId);
        }
    }

    private void enterGameChatRoom(
            final RegistrationStatus updateStatus,
            final GameMemberEntity gameMemberEntity,
            final ChatRoom chatRoom
    ) {
        final RegistrationStatus nowStatus = gameMemberEntity.getStatus();

        if (nowStatus == WAITING && updateStatus == CONFIRMED) {
            chatMessageService.enterRoomAndSaveEnteringMessages(chatRoom, memberRepository.getMemberById(gameMemberEntity.getMemberId()));
        }
    }

    @Transactional
    public void deleteGameMember(final Long loggedInMemberId, final Long gameId, final Long memberId) {
        final GameMemberDomain gameMember = gameMemberReader.readGameMemberByMemberIdAndGameId(memberId, gameId);
        final GameDomain game = gameReader.read(gameId);
        final MemberDomain member = memberReader.readByMemberId(gameMember.getMember().getMemberId());
        final MemberDomain loggedInMember = memberReader.readByMemberId(loggedInMemberId);

        if (game.isHost(loggedInMemberId)) {
            validateIsHostSelfDeleted(loggedInMember, member);
            eventPublisher.publishEvent(GameMemberRejectedEvent.builder()
                    .gameId(gameId)
                    .memberId(memberId)
                    .build());

            gameMemberWriter.deleteGameMember(gameMember);
            return;
        }

        if (loggedInMember.getMemberId().equals(member.getMemberId())) {
            cancelGameMember(gameMember);
            return;
        }

        throw new GameException(GAME_NOT_ALLOWED_TO_DELETE_GAME_MEMBER, loggedInMemberId);
    }

    private void validateIsHostSelfDeleted(final MemberDomain loggedInMember, final MemberDomain member) {
        if (loggedInMember.getMemberId().equals(member.getMemberId())) {
            throw new GameException(GAME_HOST_CANNOT_BE_DELETED, loggedInMember.getMemberId());
        }
    }

    private void cancelGameMember(final GameMemberDomain gameMember) {
        RegistrationStatus status = gameMember.getStatus();

        if (status != WAITING) {
            throw new GameException(GAME_MEMBER_STATUS_IS_NOT_WAITING, status);
        }

        gameMemberWriter.deleteGameMember(gameMember);
    }
}
