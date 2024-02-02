package kr.pickple.back.member.service;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.repository.GameMemberRepository;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.dto.response.GameMemberRegistrationStatusResponse;
import kr.pickple.back.member.dto.response.MemberGameResponse;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberGameService {

	private final MemberRepository memberRepository;
	private final GameRepository gameRepository;
	private final GameMemberRepository gameMemberRepository;
	private final MemberPositionRepository memberPositionRepository;

	/**
	 * 사용자의 참여 확정 게스트 모집글 목록 조회
	 */
	public List<MemberGameResponse> findAllMemberGames(
			final Long loggedInMemberId,
			final Long memberId,
			final RegistrationStatus memberStatus
	) {
		validateSelfMemberAccess(loggedInMemberId, memberId);

		final Member member = memberRepository.getMemberById(memberId);
		final List<GameMember> memberGames = gameMemberRepository.findAllByMemberIdAndStatus(member.getId(),
				memberStatus);

		return convertToMemberGameResponses(memberGames, memberStatus);
	}

	/**
	 * 사용자가 만든 게스트 모집글 목록 조회
	 */
	public List<MemberGameResponse> findAllCreatedGames(final Long loggedInMemberId, final Long memberId) {
		validateSelfMemberAccess(loggedInMemberId, memberId);

		final Member member = memberRepository.getMemberById(memberId);
		final List<GameMember> memberGames = gameMemberRepository.findAllByMemberId(member.getId());

		return convertToMemberGameResponses(memberGames, CONFIRMED);
	}

	/**
	 * 회원의 게스트 모집 신청 여부 조회
	 */
	public GameMemberRegistrationStatusResponse findMemberRegistrationStatusForGame(
			final Long loggedInMemberId,
			final Long memberId,
			final Long gameId
	) {
		validateSelfMemberAccess(loggedInMemberId, memberId);

		final Member member = memberRepository.getMemberById(memberId);
		final Game game = gameRepository.getGameById(gameId);

		final GameMember gameMember = gameMemberRepository.findByMemberIdAndGameId(member.getId(), game.getId())
				.orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, member.getId(), game.getId()));

		return GameMemberRegistrationStatusResponse.of(gameMember.getStatus(), gameMember.isAlreadyReviewDone());
	}

	private void validateSelfMemberAccess(Long loggedInMemberId, Long memberId) {
		if (!loggedInMemberId.equals(memberId)) {
			throw new MemberException(MEMBER_MISMATCH, loggedInMemberId, memberId);
		}
	}

	private List<MemberGameResponse> convertToMemberGameResponses(
			final List<GameMember> memberGames,
			final RegistrationStatus memberStatus
	) {
		return memberGames.stream()
				.map(memberGame ->
						MemberGameResponse.of(
								memberGame,
								getMemberResponsesByGame(memberGame.getGame(), memberStatus),
								getPositionsByMember(memberGame.getMember())
						)
				)
				.toList();
	}

	private List<MemberResponse> getMemberResponsesByGame(final Game game, final RegistrationStatus memberStatus) {
		return gameMemberRepository.findAllByGameIdAndStatus(game.getId(), memberStatus)
				.stream()
				.map(GameMember::getMember)
				.map(member -> MemberResponse.of(member, getPositionsByMember(member)))
				.toList();
	}

	private List<Position> getPositionsByMember(final Member member) {
		final List<MemberPosition> memberPositions = memberPositionRepository.findAllByMemberId(member.getId());

		return Position.fromMemberPositions(memberPositions);
	}
}
