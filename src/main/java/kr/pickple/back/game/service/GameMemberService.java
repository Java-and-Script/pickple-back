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
import kr.pickple.back.chat.repository.entity.ChatRoomEntity;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.chat.service.ChatMessageService;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.repository.entity.GameEntity;
import kr.pickple.back.game.repository.entity.GameMemberEntity;
import kr.pickple.back.game.repository.entity.GamePosition;
import kr.pickple.back.game.dto.request.GameMemberRegistrationStatusUpdateRequest;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GamePositionRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.position.domain.Position;
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

    @Transactional
    public void registerGameMember(final Long gameId, final Long loggedInMemberId) {
        final GameEntity gameEntity = gameRepository.getGameById(gameId);
        final Member member = memberRepository.getMemberById(loggedInMemberId);

        validateIsAlreadyRegisteredGameMember(gameEntity, member);
        final GameMemberEntity gameMemberEntity = buildGameMember(gameEntity, member);
        gameMemberRepository.save(gameMemberEntity);

        eventPublisher.publishEvent(GameJoinRequestNotificationEvent.builder()
                .gameId(gameId)
                .memberId(gameEntity.getHostId())
                .build());
    }

    private void validateIsAlreadyRegisteredGameMember(final GameEntity gameEntity, final Member member) {
        if (isAlreadyRegistered(gameEntity, member)) {
            throw new GameException(GAME_MEMBER_IS_EXISTED, member.getId());
        }
    }

    private boolean isAlreadyRegistered(final GameEntity gameEntity, final Member member) {
        return gameMemberRepository.findByMemberIdAndGameId(member.getId(), gameEntity.getId()).isPresent();
    }

    private GameMemberEntity buildGameMember(final GameEntity gameEntity, final Member member) {

        return GameMemberEntity.builder()
                .status(getRegistrationStatus(member, gameEntity))
                .memberId(member.getId())
                .gameId(gameEntity.getId())
                .build();
    }

    private RegistrationStatus getRegistrationStatus(final Member member, final GameEntity gameEntity) {
        final Member host = memberRepository.getMemberById(gameEntity.getHostId());

        if (member.equals(host)) {
            return CONFIRMED;
        }

        return WAITING;
    }

    public GameResponse findAllGameMembers(
            final Long loggedInMemberId,
            final Long gameId,
            final RegistrationStatus status
    ) {
        final GameMemberEntity gameMemberEntity = findGameMemberByGameIdAndMemberId(gameId, loggedInMemberId);
        final GameEntity gameEntity = gameRepository.getGameById(gameMemberEntity.getGameId());
        final Member loggedInMember = memberRepository.getMemberById(gameMemberEntity.getMemberId());

        if (!gameEntity.isHost(loggedInMember.getId()) && status == WAITING) {
            throw new GameException(GAME_MEMBER_IS_NOT_HOST, loggedInMemberId);
        }

        return GameResponse.of(
                gameEntity,
                getMemberResponsesByStatus(gameEntity, status),
                getPositionsByGame(gameEntity),
                addressReader.readMainAddressById(gameEntity.getAddressDepth1Id(), gameEntity.getAddressDepth2Id())
        );
    }

    private List<MemberResponse> getMemberResponsesByStatus(final GameEntity gameEntity, final RegistrationStatus status) {
        return gameMemberRepository.findAllByGameIdAndStatus(gameEntity.getId(), status)
                .stream()
                .map(gameMember -> memberRepository.getMemberById(gameMember.getMemberId()))
                .map(member -> MemberResponse.of(
                                member,
                                getPositionsByMember(member),
                                addressReader.readMainAddressById(member.getAddressDepth1Id(), member.getAddressDepth2Id())
                        )
                )
                .toList();
    }

    private List<Position> getPositionsByMember(final Member member) {
        final List<MemberPosition> memberPositions = memberPositionRepository.findAllByMemberId(
                member.getId());

        return Position.fromMemberPositions(memberPositions);
    }

    private List<Position> getPositionsByGame(final GameEntity gameEntity) {
        final List<GamePosition> gamePositions = gamePositionRepository.findAllByGameId(gameEntity.getId());

        return Position.fromGamePositions(gamePositions);
    }

    private GameMemberEntity findGameMemberByGameIdAndMemberId(final Long gameId, final Long memberId) {
        return gameMemberRepository.findByMemberIdAndGameId(memberId, gameId)
                .orElseThrow(() -> new GameException(GAME_MEMBER_NOT_FOUND, gameId, memberId));
    }

    @Transactional
    public void updateGameMemberRegistrationStatus(
            final Long loggedInMemberId,
            final Long gameId,
            final Long memberId,
            final GameMemberRegistrationStatusUpdateRequest gameMemberRegistrationStatusUpdateRequest
    ) {
        final GameMemberEntity gameMemberEntity = findGameMemberByGameIdAndMemberId(gameId, memberId);
        final GameEntity gameEntity = gameRepository.getGameById(gameMemberEntity.getGameId());

        validateIsHost(loggedInMemberId, gameEntity);

        final RegistrationStatus updateStatus = gameMemberRegistrationStatusUpdateRequest.getStatus();
<<<<<<< HEAD
        final ChatRoomEntity chatRoom = chatRoomRepository.getChatRoomById(game.getChatRoomId());
        enterGameChatRoom(updateStatus, gameMember, chatRoom);
=======
        final ChatRoom chatRoom = chatRoomRepository.getChatRoomById(gameEntity.getChatRoomId());
        enterGameChatRoom(updateStatus, gameMemberEntity, chatRoom);
>>>>>>> ab89fd2 (refactor: 바뀐 구현계층 세팅에 맞추어 게임service의 생성, 읽기 기능 수정)

        gameMemberEntity.updateStatus(updateStatus);
        if (gameMemberEntity.isStatusChangedFromWaitingToConfirmed(updateStatus)) {
            gameEntity.increaseMemberCount();
        }

        eventPublisher.publishEvent(GameMemberJoinedEvent.builder()
                .gameId(gameId)
                .memberId(memberId)
                .build());
    }

    private void validateIsHost(final Long loggedInMemberId, final GameEntity gameEntity) {
        final Member loggedInMember = memberRepository.getMemberById(loggedInMemberId);

        if (!gameEntity.isHost(loggedInMember.getId())) {
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
        final GameMemberEntity gameMemberEntity = findGameMemberByGameIdAndMemberId(gameId, memberId);
        final GameEntity gameEntity = gameRepository.getGameById(gameMemberEntity.getGameId());
        final Member member = memberRepository.getMemberById(gameMemberEntity.getMemberId());
        final Member loggedInMember = memberRepository.getMemberById(loggedInMemberId);

        if (gameEntity.isHost(loggedInMember.getId())) {
            validateIsHostSelfDeleted(loggedInMember, member);

            eventPublisher.publishEvent(GameMemberRejectedEvent.builder()
                    .gameId(gameId)
                    .memberId(memberId)
                    .build());

            deleteGameMember(gameMemberEntity);

            return;
        }

        if (loggedInMember.equals(member)) {
            cancelGameMember(gameMemberEntity);

            return;
        }

        throw new GameException(GAME_NOT_ALLOWED_TO_DELETE_GAME_MEMBER, loggedInMemberId);
    }

    private void validateIsHostSelfDeleted(final Member loggedInMember, final Member member) {
        if (loggedInMember.equals(member)) {
            throw new GameException(GAME_HOST_CANNOT_BE_DELETED, loggedInMember.getId());
        }
    }

    private void cancelGameMember(final GameMemberEntity gameMemberEntity) {
        RegistrationStatus status = gameMemberEntity.getStatus();

        if (status != WAITING) {
            throw new GameException(GAME_MEMBER_STATUS_IS_NOT_WAITING, status);
        }

        deleteGameMember(gameMemberEntity);
    }

    private void deleteGameMember(final GameMemberEntity gameMemberEntity) {
        gameMemberRepository.delete(gameMemberEntity);
    }
}
