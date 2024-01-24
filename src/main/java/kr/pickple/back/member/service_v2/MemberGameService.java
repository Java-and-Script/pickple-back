package kr.pickple.back.member.service_v2;

import static kr.pickple.back.common.domain.RegistrationStatus.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.domain.GameMember;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.dto.response.GameMemberRegistrationStatusResponse;
import kr.pickple.back.member.dto.response.MemberGameResponse;
import kr.pickple.back.member.dto.response.MemberResponse;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberGameService {

    private final MemberRepository memberRepository;
    private final GameRepository gameRepository;

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
        final List<GameMember> memberGames = member.getMemberGamesByStatus(memberStatus);

        return convertToMemberGameResponses(memberGames, memberStatus);
    }

    /**
     * 사용자가 만든 게스트 모집글 목록 조회
     */
    public List<MemberGameResponse> findAllCreatedGames(final Long loggedInMemberId, final Long memberId) {
        validateSelfMemberAccess(loggedInMemberId, memberId);

        final Member member = memberRepository.getMemberById(memberId);
        final List<GameMember> memberGames = member.getCreatedMemberGames();

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

        final RegistrationStatus memberRegistrationStatus = member.findGameRegistrationStatus(game);
        final Boolean isReviewDone = member.isAlreadyReviewDoneInGame(game);

        return GameMemberRegistrationStatusResponse.of(memberRegistrationStatus, isReviewDone);
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
                .map(memberGame -> MemberGameResponse.of(
                        memberGame,
                        getMemberResponsesByGame(memberGame.getGame(), memberStatus)
                ))
                .toList();
    }

    private List<MemberResponse> getMemberResponsesByGame(final Game game, final RegistrationStatus memberStatus) {
        return game.getMembersByStatus(memberStatus)
                .stream()
                .map(MemberResponse::from)
                .toList();
    }
}
