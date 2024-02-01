package kr.pickple.back.game.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.game.exception.GameExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.alarm.event.game.GameJoinRequestNotificationEvent;
import kr.pickple.back.alarm.event.game.GameMemberJoinedEvent;
import kr.pickple.back.alarm.event.game.GameMemberRejectedEvent;
import kr.pickple.back.chat.service.ChatMessageService;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.dto.request.GameMemberRegistrationStatusUpdateRequest;
import kr.pickple.back.game.dto.response.GameResponse;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameMemberService {

	private final GameRepository gameRepository;
	private final MemberRepository memberRepository;
	private final ChatMessageService chatMessageService;
	private final ApplicationEventPublisher eventPublisher;
	private final GameMemberRepository gameMemberRepository;

	@Transactional
	public void registerGameMember(final Long gameId, final Long loggedInMemberId) {
		final Game game = findGameById(gameId);
		final Member member = findMemberById(loggedInMemberId);

		game.addGameMember(member);

		eventPublisher.publishEvent(GameJoinRequestNotificationEvent.builder()
				.gameId(gameId)
				.memberId(game.getHost().getId())
				.build());
	}

	private Game findGameById(final Long gameId) {
		return gameRepository.findById(gameId)
				.orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));
	}

	public GameResponse findAllGameMembers(
			final Long loggedInMemberId,
			final Long gameId,
			final RegistrationStatus status
	) {
		final GameMember gameMember = findGameMemberByGameIdAndMemberId(gameId, loggedInMemberId);
		final Game game = gameMember.getGame();
		final Member loggedInMember = gameMember.getMember();

		if (!game.isHost(loggedInMember) && status == WAITING) {
			throw new GameException(GAME_MEMBER_IS_NOT_HOST, loggedInMemberId);
		}

		return GameResponse.of(game, getMemberResponses(game, status));
	}

	private List<MemberResponse> getMemberResponses(final Game game, final RegistrationStatus status) {
		return game.getMembersByStatus(status)
				.stream()
				.map(MemberResponse::from)
				.toList();
	}

	private GameMember findGameMemberByGameIdAndMemberId(final Long gameId, final Long memberId) {
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
		final GameMember gameMember = findGameMemberByGameIdAndMemberId(gameId, memberId);
		final Game game = gameMember.getGame();

		validateIsHost(loggedInMemberId, game);

		final RegistrationStatus updateStatus = gameMemberRegistrationStatusUpdateRequest.getStatus();
		enterGameChatRoom(updateStatus, gameMember);

		gameMember.updateStatus(updateStatus);

		eventPublisher.publishEvent(GameMemberJoinedEvent.builder()
				.gameId(gameId)
				.memberId(memberId)
				.build());
	}

	private void validateIsHost(final Long loggedInMemberId, final Game game) {
		final Member loggedInMember = findMemberById(loggedInMemberId);

		if (!game.isHost(loggedInMember)) {
			throw new GameException(GAME_MEMBER_IS_NOT_HOST, loggedInMemberId);
		}
	}

	private void enterGameChatRoom(final RegistrationStatus updateStatus, final GameMember gameMember) {
		final RegistrationStatus nowStatus = gameMember.getStatus();

		if (nowStatus == WAITING && updateStatus == CONFIRMED) {
			chatMessageService.enterRoomAndSaveEnteringMessages(gameMember.getCrewChatRoom(), gameMember.getMember());
		}
	}

	@Transactional
	public void deleteGameMember(final Long loggedInMemberId, final Long gameId, final Long memberId) {
		final GameMember gameMember = findGameMemberByGameIdAndMemberId(gameId, memberId);
		final Game game = gameMember.getGame();
		final Member member = gameMember.getMember();
		final Member loggedInMember = findMemberById(loggedInMemberId);

		if (game.isHost(loggedInMember)) {
			validateIsHostSelfDeleted(loggedInMember, member);

			eventPublisher.publishEvent(GameMemberRejectedEvent.builder()
					.gameId(gameId)
					.memberId(memberId)
					.build());

			deleteGameMember(gameMember);

			return;
		}

		if (loggedInMember.equals(member)) {
			cancelGameMember(gameMember);

			return;
		}

		throw new GameException(GAME_NOT_ALLOWED_TO_DELETE_GAME_MEMBER, loggedInMemberId);
	}

	private Member findMemberById(final Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
	}

	private void validateIsHostSelfDeleted(final Member loggedInMember, final Member member) {
		if (loggedInMember.equals(member)) {
			throw new GameException(GAME_HOST_CANNOT_BE_DELETED, loggedInMember.getId());
		}
	}

	private void cancelGameMember(final GameMember gameMember) {
		RegistrationStatus status = gameMember.getStatus();

		if (status != WAITING) {
			throw new GameException(GAME_MEMBER_STATUS_IS_NOT_WAITING, status);
		}

		deleteGameMember(gameMember);
	}

	private void deleteGameMember(final GameMember gameMember) {
		gameMemberRepository.delete(gameMember);
	}
}
